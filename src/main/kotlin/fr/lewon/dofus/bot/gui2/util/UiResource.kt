package fr.lewon.dofus.bot.gui2.util

import androidx.compose.ui.graphics.toPainter

enum class UiResource(path: String, filledPath: String = path) {

    GLOBAL_LOGO("/icon/global_logo.png"),
    TASKBAR_LOGO("/icon/taskbar_logo.png"),
    SCRIPT_LOGO("/icon/script_logo.png"),
    METAMOB_HELPER_LOGO("/icon/metamob_helper.png"),
    EXPLORATION_HELPER_LOGO("/icon/exploration_helper.png"),
    SETTINGS_LOGO("/icon/settings_logo.png"),
    CONFIGURE_CHARACTER("/icon/ui/configure_character.png"),
    DELETE("/icon/ui/delete.png", "/icon/ui/delete_filled.png"),
    STOP("/icon/ui/stop.png", "/icon/ui/stop_filled.png"),
    PLAY_ARROW("/icon/ui/play_arrow.png", "/icon/ui/play_arrow_filled.png"),
    PAUSE("/icon/ui/pause.png", "/icon/ui/pause_filled.png"),
    PLUS("/icon/ui/plus.png"),
    MINUS("/icon/ui/minus.png"),
    AUTO_SCROLL("/icon/ui/auto_scroll.png", "/icon/ui/auto_scroll_filled.png"),
    ERASE("/icon/ui/erase.png", "/icon/ui/erase_filled.png"),
    HISTORY("/icon/ui/history.png", "/icon/ui/history_filled.png"),
    BLACK_CIRCLE("/icon/ui/black_circle.png", "/icon/ui/black_circle.png"),
    RED_CIRCLE("/icon/ui/red_circle.png"),
    GREEN_CIRCLE("/icon/ui/green_circle.png"),
    ORANGE_CIRCLE("/icon/ui/orange_circle.png"),
    MENU("/icon/ui/menu.png", "/icon/ui/menu_filled.png"),
    REFRESH("/icon/ui/refresh.png", "/icon/ui/refresh_filled.png"),
    RETRY("/icon/ui/retry.png"),
    ;

    val imageData = javaClass.getResourceAsStream(path)?.readAllBytes()
        ?: error("Couldn't find image [$path]")
    val filledImageData = javaClass.getResourceAsStream(filledPath)?.readAllBytes()
        ?: error("Couldn't find filled image [$filledPath]")
    val image = imageData.getBufferedImage()
    val filledImage = filledImageData.getBufferedImage()
    val imagePainter = image.toPainter()
    val filledImagePainter = filledImage.toPainter()
}
