package fr.lewon.dofus.bot.core.d2o.gamedata

enum class GameDataTypeEnum(val id: Int) {

    INT(-1),
    BOOLEAN(-2),
    STRING(-3),
    NUMBER(-4),
    I18N(-5),
    UINT(-6),
    VECTOR(-99);

    companion object {

        fun getDataType(type: Int): GameDataTypeEnum? {
            return entries.firstOrNull { it.id == type }
        }

    }

}