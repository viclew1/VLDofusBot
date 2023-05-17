package fr.lewon.dofus.bot.core.ui.xml.sizes

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute

@XmlAccessorType(XmlAccessType.FIELD)
data class Dimension(
    @field:XmlAttribute var x: String = "",
    @field:XmlAttribute var y: String = ""
) {
    fun deepCopy(): Dimension {
        return Dimension(x, y)
    }
}