package fr.lewon.dofus.bot.model.characters

import androidx.compose.ui.graphics.toPainter
import fr.lewon.dofus.bot.core.model.charac.DofusBreed
import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.gui.util.getBufferedImage


class DofusBreedAssets(val breed: DofusBreed) {

    private val iconFileName = "icon/symbol_${breed.id}.png"
    private val bannerFileName = "banner/banner_${breed.id}.png"
    private val simpleIconFileName = "simple_icon/${breed.id}0.png"

    private val basePath = "/icon/classes/"
    private val simpleIconData = javaClass.getResourceAsStream(basePath + simpleIconFileName)?.readAllBytes()
        ?: error("Couldn't find simple icon [$simpleIconFileName]")
    val iconData = javaClass.getResourceAsStream(basePath + iconFileName)?.readAllBytes()
        ?: error("Couldn't find icon [$iconFileName]")
    val bannerData = javaClass.getResourceAsStream(basePath + bannerFileName)?.readAllBytes()
        ?: error("Couldn't find banner [$bannerFileName]")
    val simpleIcon = simpleIconData.getBufferedImage()
    val iconImage = iconData.getBufferedImage()
    val blurredIconImage = ImageUtil.blurImage(iconImage, 0.6f)
    val bannerImage = bannerData.getBufferedImage()
    val simpleIconPainter = simpleIcon.toPainter()
    val iconPainter = iconImage.toPainter()
    val blurredIconPainter = blurredIconImage.toPainter()
    val bannerPainter = bannerImage.toPainter()
}