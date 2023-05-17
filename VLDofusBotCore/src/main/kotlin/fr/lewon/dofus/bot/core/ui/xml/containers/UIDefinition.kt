package fr.lewon.dofus.bot.core.ui.xml.containers

import fr.lewon.dofus.bot.core.ui.xml.Import
import fr.lewon.dofus.bot.core.ui.xml.constants.Constant
import fr.lewon.dofus.bot.core.ui.xml.constants.Param
import fr.lewon.dofus.bot.core.ui.xml.constants.Var
import org.w3c.dom.Element
import javax.xml.bind.annotation.*

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Definition")
data class UIDefinition(
    @field:XmlElement(name = "Import")
    var imports: ArrayList<Import> = ArrayList(),

    @field:XmlElementWrapper(name = "Constants")
    @field:XmlElement(name = "Constant")
    var constants: ArrayList<Constant> = ArrayList(),

    @field:XmlElement(name = "Var")
    var vars: ArrayList<Var> = ArrayList(),

    @field:XmlElement(name = "Param")
    var params: ArrayList<Param> = ArrayList(),

    @field:XmlAnyElement
    var childrenPremises: ArrayList<Element> = ArrayList()
) {

    val children = ArrayList<Container>()

    fun deepCopy(): UIDefinition {
        return UIDefinition(
            ArrayList(imports.map { it.deepCopy() }),
            ArrayList(constants.map { it.deepCopy() }),
            ArrayList(vars.map { it.deepCopy() }),
            ArrayList(params.map { it.deepCopy() }),
        ).also { copy -> copy.children.addAll(children.map { child -> child.deepCopy() }) }
    }

}