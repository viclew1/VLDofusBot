package fr.lewon.dofus.bot.gui.util

import androidx.compose.ui.graphics.toPainter

enum class UiResource(path: String) {

    GLOBAL_LOGO("/icon/global_logo.png"),
    TASKBAR_LOGO("/icon/taskbar_logo.png"),
    SCRIPT_LOGO("/icon/script_logo.png"),
    METAMOB_HELPER_LOGO("/icon/metamob_helper.png"),
    EXPLORATION_HELPER_LOGO("/icon/exploration_helper.png"),
    AH_ITEM_FINDER_LOGO("/icon/auction_house_item_finder.png"),
    DEV_TOOLS_LOGO("/icon/dev_tools_logo.png"),
    SETTINGS_LOGO("/icon/settings_logo.png"),
    GITHUB("/icon/github.png"),
    DISCORD("/icon/discord.png"),
    PAUSE("/icon/ui/pause.png"),
    AUTO_SCROLL("/icon/ui/auto_scroll.png"),
    CHECK("/icon/ui/check.png"),
    GATHER("/icon/ui/gather.png"),
    ERASE("/icon/ui/erase.png"),
    KAMAS("/icon/ui/kamas.png"),
    LEVEL("/icon/ui/level.png"),
    WEIGHT("/icon/ui/weight.png"),
    SPELLS("/icon/ui/spells.png"),
    CHAT("/icon/ui/chat.png"),
    LOWER_LAYER("/icon/ui/lower_layer.png"),
    UPPER_LAYER("/icon/ui/upper_layer.png"),
    JOBS("/icon/ui/jobs.png"),
    MONSTERS("/icon/ui/monsters.png"),
    ZAAP("/icon/ui/zaap.png"),
    UNKNOWN("/icon/ui/unknown.png"),
    WARNING("/icon/ui/warning.png"),
    TREASURE_HUNT("/icon/ui/treasure_hunt.png"),
    ARCHMONSTER("/icon/ui/archmonster.png"),
    QUEST_MONSTER("/icon/ui/quest_monster.png"),
    ;

    val imageData = javaClass.getResourceAsStream(path)?.readAllBytes()
        ?: error("Couldn't find image [$path]")
    val image = imageData.getBufferedImage()
    val imagePainter = image.toPainter()
}
