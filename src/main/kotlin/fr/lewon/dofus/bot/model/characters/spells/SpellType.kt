package fr.lewon.dofus.bot.model.characters.spells

import fr.lewon.dofus.bot.gui.custom.listrenderer.TextImageListItem

enum class SpellType(private val label: String, iconFileName: String) : TextImageListItem {
    NAMED_SPELL("Named Spell", "feature_67.png"),
    CUSTOM_MP_BUFF("Custom MP Buff", "feature_204.png"),
    CUSTOM_GAP_CLOSER("Custom Gap Closer", "feature_25.png");

    private val basePath = "/icon/menu_icons/"
    private val iconData = javaClass.getResourceAsStream(basePath + iconFileName)?.readAllBytes()
        ?: error("Couldn't find icon [$iconFileName]")

    override fun getListImageIconData(): ByteArray {
        return iconData
    }

    override fun getIndex(): Int {
        return ordinal
    }

    override fun getLabel(): String {
        return label
    }
}