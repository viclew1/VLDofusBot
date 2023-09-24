package fr.lewon.dofus.bot.scripts.tasks.impl.harvest

enum class TransferItemsToBankBehaviour(val strValue: String, val optionIndex: Int) {
    TransferAllVisibleObjects("Transfer all visible objects", 1),
    TransferAlreadyPresentObjects("Transfer only already present objects", 2)
}