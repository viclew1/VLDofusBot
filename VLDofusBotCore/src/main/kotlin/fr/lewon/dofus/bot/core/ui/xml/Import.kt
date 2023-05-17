package fr.lewon.dofus.bot.core.ui.xml

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute

@XmlAccessorType(XmlAccessType.FIELD)
data class Import(
    @field:XmlAttribute var url: String = "",
) {
    fun deepCopy(): Import {
        return Import(url)
    }
}