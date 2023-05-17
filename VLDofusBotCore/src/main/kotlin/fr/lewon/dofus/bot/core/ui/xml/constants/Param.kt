package fr.lewon.dofus.bot.core.ui.xml.constants

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlValue

@XmlAccessorType(XmlAccessType.FIELD)
data class Param(
    @field:XmlAttribute var name: String = "",
    @field:XmlValue var value: String = ""
) {
    fun deepCopy(): Param {
        return Param(name, value)
    }
}