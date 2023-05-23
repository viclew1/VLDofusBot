package fr.lewon.dofus.bot.core.model.charac

data class DofusCharacteristic(
    val id: Int,
    val name: String,
    val order: Int,
    val categoryId: Int,
    val keyWord: String
)