package fr.lewon.dofus.bot.gui2.util

import androidx.compose.ui.graphics.toPainter

enum class UiResource(path: String) {

    GLOBAL_LOGO("/icon/global_logo.png"),
    TASKBAR_LOGO("/icon/taskbar_logo.png"),
    SCRIPT_LOGO("/icon/script_logo.png"),
    METAMOB_HELPER_LOGO("/icon/metamob_helper.png"),
    EXPLORATION_HELPER_LOGO("/icon/exploration_helper.png"),
    SETTINGS_LOGO("/icon/settings_logo.png"),
    PAUSE("/icon/ui/pause.png"),
    AUTO_SCROLL("/icon/ui/auto_scroll.png"),
    ERASE("/icon/ui/erase.png"),
    KAMAS("/icon/ui/kamas.png"),
    LEVEL("/icon/ui/level.png"),
    WEIGHT("/icon/ui/weight.png"),
    SPELLS("/icon/ui/spells.png"),
    CHAT("/icon/ui/chat.png"),
    ZAAP("/icon/ui/zaap.png")
    ;

    val imageData = javaClass.getResourceAsStream(path)?.readAllBytes()
        ?: error("Couldn't find image [$path]")
    val image = imageData.getBufferedImage()
    val imagePainter = image.toPainter()
}
