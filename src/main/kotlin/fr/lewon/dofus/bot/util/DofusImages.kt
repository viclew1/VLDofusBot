package fr.lewon.dofus.bot.util

enum class DofusImages(imageName: String) {

    CHECKPOINT_BTN("checkpoint.png"),
    SEARCH_BTN("search.png"),
    FIGHT_BTN("fight.png"),
    BL_PHORROR("phoBL_template.png"),
    BR_PHORROR("phoBR_template.png"),
    TL_PHORROR("phoTL_template.png"),
    TR_PHORROR("phoTR_template.png"),
    ARCHI_MONSTER("archimonster.png"),
    TREASURE_HUNT_FRAME_TOP_TEMPLATE("hunt_frame_top.png"),
    TREASURE_HUNT_FRAME_BOT_TEMPLATE("hunt_frame_bot.png"),
    TREASURE_HUNT_FRAME_LEFT_TEMPLATE("hunt_frame_left.png"),
    TREASURE_HUNT_FRAME_RIGHT_TEMPLATE("hunt_frame_right.png"),
    RUNEFORGE_FRAME_TOP_TEMPLATE("runeforge_frame_top.png"),
    RUNEFORGE_FRAME_BOT_TEMPLATE("runeforge_frame_bot.png"),
    RUNEFORGE_FRAME_LEFT_TEMPLATE("runeforge_frame_left.png"),
    RUNEFORGE_FRAME_RIGHT_TEMPLATE("runeforge_frame_right.png");

    val path: String = "templates/$imageName"

}