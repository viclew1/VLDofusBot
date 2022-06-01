package fr.lewon.dofus.bot.gui.util

enum class UiResource(path: String, filledPath: String) {

    VLDB_LOGO("/icon/logo.png", "/icon/logo.png"),
    METAMOB_HELPER_LOGO("/icon/metamob_helper.png", "/icon/metamob_helper.png"),
    CONFIGURE_CHARACTER("/icon/ui/configure_character.png", "/icon/ui/configure_character_filled.png"),
    DELETE("/icon/ui/delete.png", "/icon/ui/delete_filled.png"),
    STOP("/icon/ui/stop.png", "/icon/ui/stop_filled.png"),
    PLAY_ARROW("/icon/ui/play_arrow.png", "/icon/ui/play_arrow_filled.png"),
    PAUSE("/icon/ui/pause.png", "/icon/ui/pause_filled.png"),
    PLUS("/icon/ui/plus.png", "/icon/ui/plus.png"),
    MINUS("/icon/ui/minus.png", "/icon/ui/minus.png"),
    AUTO_SCROLL("/icon/ui/auto_scroll.png", "/icon/ui/auto_scroll_filled.png"),
    ERASE("/icon/ui/erase.png", "/icon/ui/erase_filled.png"),
    HISTORY("/icon/ui/history.png", "/icon/ui/history_filled.png"),
    BLACK_CIRCLE("/icon/ui/black_circle.png", "/icon/ui/black_circle.png"),
    RED_CIRCLE("/icon/ui/red_circle.png", "/icon/ui/red_circle.png"),
    GREEN_CIRCLE("/icon/ui/green_circle.png", "/icon/ui/green_circle.png"),
    ORANGE_CIRCLE("/icon/ui/orange_circle.png", "/icon/ui/orange_circle.png"),
    MENU("/icon/ui/menu.png", "/icon/ui/menu_filled.png"),
    ;

    val imageData = javaClass.getResourceAsStream(path)?.readAllBytes()
        ?: error("Couldn't find image [$path]")
    val filledImageData = javaClass.getResourceAsStream(filledPath)?.readAllBytes()
        ?: error("Couldn't find filled image [$filledPath]")

}