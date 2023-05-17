package fr.lewon.dofus.bot.core.ui.xml.containers

import fr.lewon.dofus.bot.core.ui.xml.constants.Constant
import fr.lewon.dofus.bot.core.ui.xml.constants.Param
import fr.lewon.dofus.bot.core.ui.xml.constants.Var
import javax.xml.bind.annotation.*

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "PROPERTIES_HOLDER")
data class PropertiesHolder(
    @field:XmlElementWrapper(name = "Constants")
    @field:XmlElement(name = "Constant")
    var constants: ArrayList<Constant> = ArrayList(),

    @field:XmlElement(name = "Var")
    var vars: ArrayList<Var> = ArrayList(),

    @field:XmlElement(name = "Param")
    var params: ArrayList<Param> = ArrayList(),
) {
    fun parseValues(str: String): String {
        var parsedStr = str
        for (property in constants) {
            parsedStr = parsedStr.replace("[local.${property.name}]", property.value)
        }
        for (property in params) {
            parsedStr = parsedStr.replace("#${property.name}", property.value)
        }
        for (property in vars) {
            parsedStr = parsedStr.replace("\$${property.name}", property.value)
        }
        return parsedStr
    }
}