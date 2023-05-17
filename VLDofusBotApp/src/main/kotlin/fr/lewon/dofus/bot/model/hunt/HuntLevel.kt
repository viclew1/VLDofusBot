package fr.lewon.dofus.bot.model.hunt

enum class HuntLevel(val skillId: Int, val label: String) {
    LVL200(257, "200"),
    LVL180(256, "180"),
    LVL160(255, "160"),
    LVL140(254, "140"),
    LVL120(253, "120"),
    LVL100(252, "100"),
    LVL80(251, "80"),
    LVL60(250, "60"),
    LVL40(249, "40"),
    LVL20(248, "20");

    companion object {
        fun fromLabel(label: String): HuntLevel? {
            return values().firstOrNull { it.label == label }
        }
    }
}