package fr.lewon.dofus.bot.gui.util

import java.net.URL

enum class UiResource(path: String, filledPath: String) {

    EDIT("/icon/ui/edit.png", "/icon/ui/edit_filled.png"),
    DELETE("/icon/ui/delete.png", "/icon/ui/delete_filled.png");

    val url: URL = javaClass.getResource(path) ?: error("Couldn't find image [$path]")
    val filledUrl: URL = javaClass.getResource(filledPath) ?: error("Couldn't find filled image [$filledPath]")

}