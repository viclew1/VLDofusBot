package fr.lewon.dofus.bot.core.ui.xml.anchors

enum class AnchorPoint(
    val widthRatio: Float,
    val heightRatio: Float
) {
    BOTTOMRIGHT(1f, 1f),
    BOTTOMLEFT(0f, 1f),
    TOPRIGHT(1f, 0f),
    TOPLEFT(0f, 0f),
    TOP(0.5f, 0f),
    BOTTOM(0.5f, 1f),
    RIGHT(1f, 0.5f),
    LEFT(0f, 0.5f),
    CENTER(0.5f, 0.5f);

}