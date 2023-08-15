package fr.lewon.dofus.bot.gui.main.characters.edit.sets.bar

data class ElementDrag<T>(
    val itemType: ElementItemType,
    val element: T,
    val fromKey: ElementKey? = null,
)