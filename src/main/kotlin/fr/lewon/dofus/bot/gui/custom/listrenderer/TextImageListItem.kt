package fr.lewon.dofus.bot.gui.custom.listrenderer

interface TextImageListItem {

    fun getListImageIcon(): ByteArray
    fun getIndex(): Int
    fun getLabel(): String

}