package fr.lewon.dofus.bot.gui.custom.listrenderer

interface TextImageListItem {

    fun getListImageIconData(): ByteArray
    fun getIndex(): Int
    fun getLabel(): String

}