@file:JvmName("XmlContainerInitializerKt")

package fr.lewon.dofus.bot.core.ui.managers

import fr.lewon.dofus.bot.core.ui.xml.XmlRootElementName
import fr.lewon.dofus.bot.core.ui.xml.constants.Var
import fr.lewon.dofus.bot.core.ui.xml.containers.Container
import fr.lewon.dofus.bot.core.ui.xml.containers.PropertiesHolder
import fr.lewon.dofus.bot.core.ui.xml.containers.UIDefinition
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.File
import java.io.StringWriter
import java.util.concurrent.atomic.AtomicInteger
import javax.xml.bind.JAXBContext
import javax.xml.bind.Unmarshaller
import javax.xml.parsers.SAXParserFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.stream.StreamResult


object XmlUiUtil {

    private val TEMPLATE_INSTANCE_ID_GENERATORS = HashMap<String, AtomicInteger>()

    private val VALID_CONTAINER_TAG_NAMES = listOf(
        "Container", "Texture", "Label", "Button", "TextureBitmap", "Grid", "Common", "ComboBox"
    )

    private val TO_REMOVE_TAG = listOf<String>()

    private val SPF = SAXParserFactory.newInstance().also {
        it.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        it.setFeature("http://xml.org/sax/features/validation", false)
    }
    private val XML_READER = SPF.newSAXParser().xmlReader


    private val TRANS_FACTORY = TransformerFactory.newInstance()
    private val TRANSFORMER: Transformer = TRANS_FACTORY.newTransformer().also {
        it.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
    }

    private val UNMARSHALLER_BY_CLASS = HashMap<Class<*>, Unmarshaller>()

    private val SUB_CONTAINER_STR_BY_NAME = HashMap<String, String>()
    private val UI_DEFINITION_BY_NAME = HashMap<String, UIDefinition>()

    fun getUIDefinition(fileName: String): UIDefinition {
        return UI_DEFINITION_BY_NAME[fileName]?.deepCopy()
            ?: error("No container for file name : $fileName")
    }

    fun init(xmlFile: File) {
        var xmlContent = xmlFile.readText()
        for (toRemoveTag in TO_REMOVE_TAG) {
            xmlContent = xmlContent.replace("<$toRemoveTag>", "")
                .replace("</$toRemoveTag>", "")
        }
        val definitionTagName = XmlRootElementName.UI_DEFINITION.tagName
        if (xmlContent.contains("<$definitionTagName", true) && xmlContent.contains("</$definitionTagName", true)) {
            val propertiesContent = replaceTagName(xmlContent, definitionTagName, XmlRootElementName.PROPERTIES_HOLDER)
            val propertiesHolder: PropertiesHolder = xmlToObject(propertiesContent)
            val parsedXmlContent = propertiesHolder.parseValues(xmlContent)
            UI_DEFINITION_BY_NAME[xmlFile.name] = xmlToObject(parsedXmlContent)
        } else {
            SUB_CONTAINER_STR_BY_NAME[xmlFile.name] = xmlContent
        }
    }

    private inline fun <reified T> xmlToObject(xmlContent: String): T {
        val inputSource = InputSource(xmlContent.byteInputStream())
        val source = SAXSource(XML_READER, inputSource)
        val unmarshaller = UNMARSHALLER_BY_CLASS.computeIfAbsent(T::class.java) {
            JAXBContext.newInstance(T::class.java).createUnmarshaller()
        }
        return unmarshaller.unmarshal(source) as T
    }

    fun initAllContainers() {
        UI_DEFINITION_BY_NAME.entries.filter { DofusUIElement.shouldInitializeXml(it.key) }.forEach {
            val uiDefinition = it.value
            for (childPremise in uiDefinition.childrenPremises) {
                buildContainer(childPremise)?.let { container -> uiDefinition.children.add(container) }
            }
        }
    }

    private fun buildContainer(element: Element): Container? {
        val tagName = element.tagName
        val subContainerStr = SUB_CONTAINER_STR_BY_NAME["$tagName.xml"]
        val container: Container = when {
            tagName in VALID_CONTAINER_TAG_NAMES -> buildStandardContainer(element)
            subContainerStr != null -> buildSubContainer(element, subContainerStr)
            else -> return null
        }
        container.childrenPremises.forEach {
            if (it.childNodes.length > 1) {
                buildContainer(it)?.let { childContainer -> container.children.add(childContainer) }
            }
        }
        return container
    }

    private fun buildSubContainer(element: Element, subContainerStr: String): Container {
        val tagName = element.tagName
        val params = buildParams(element)
        val propertiesHolderContent = replaceTagName(subContainerStr, tagName, XmlRootElementName.PROPERTIES_HOLDER)
        val propertiesHolder: PropertiesHolder = xmlToObject(propertiesHolderContent)
        val instanceId = TEMPLATE_INSTANCE_ID_GENERATORS.computeIfAbsent(tagName) { AtomicInteger(0) }
            .incrementAndGet()
        propertiesHolder.vars.add(0, Var("TEMPLATE_INSTANCE_ID", tagName + instanceId.toString()))
        applyParams(params, propertiesHolder)
        val parsedXmlContent = propertiesHolder.parseValues(subContainerStr)
        val containerContent = replaceTagName(parsedXmlContent, tagName, XmlRootElementName.CONTAINER)
        return xmlToObject(containerContent)
    }

    private fun buildStandardContainer(element: Element): Container {
        return xmlToObject(replaceTagName(getNodeTextContent(element), element.tagName, XmlRootElementName.CONTAINER))
    }

    private fun replaceTagName(xmlContent: String, tagName: String, newRootElementName: XmlRootElementName): String {
        return xmlContent.replaceFirst("<$tagName", "<${newRootElementName.tagName}", true)
            .replaceAfterLast("<", "/${newRootElementName.tagName}>")
    }

    private fun applyParams(params: Map<String, String>, propertiesHolder: PropertiesHolder) {
        for ((name, value) in params.entries) {
            propertiesHolder.params.firstOrNull { it.name == name }?.value = value
        }
        for (property in propertiesHolder.params) {
            property.value = propertiesHolder.parseValues(property.value)
        }
        for (variable in propertiesHolder.vars) {
            variable.value = XmlVarParser.parse(variable.value, propertiesHolder)
        }
    }

    private fun buildParams(element: Element): Map<String, String> {
        val params = HashMap<String, String>()
        for (i in 0 until element.childNodes.length - 1) {
            val child = element.childNodes.item(i)
            params[child.nodeName] = when {
                child.childNodes.length >= 2 -> {
                    val value = getNodeTextContent(child)
                    value.substring(child.nodeName.length + 2, value.length - child.nodeName.length - 3)
                }
                child.childNodes.length == 1 -> child.textContent
                else -> ""
            }
        }
        for (i in 0 until element.attributes.length) {
            val attribute = element.attributes.item(i)
            params[attribute.nodeName] = attribute.textContent
        }
        return params
    }

    private fun getNodeTextContent(node: Node): String {
        val buffer = StringWriter()
        TRANSFORMER.transform(DOMSource(node), StreamResult(buffer))
        return buffer.toString()
    }

}