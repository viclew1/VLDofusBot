package fr.lewon.dofus.bot.model.characters

import fr.lewon.dofus.bot.core.model.charac.DofusBreed
import fr.lewon.dofus.bot.gui.custom.listrenderer.TextImageListItem


class DofusBreedAssets(val breed: DofusBreed) : TextImageListItem {

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

    override fun getListImageIconData(): ByteArray {
        return simpleIconData
    }

    override fun getIndex(): Int {
        return breed.id - 1
    }

    override fun getLabel(): String {
        return breed.name
    }

    override fun toString(): String {
        return getLabel()
    }
}