package fr.lewon.dofus.bot.game.classes

import java.net.URL


enum class DofusClass(
    iconFileName: String,
    bannerFileName: String,
    simpleIconFileName: String,
    val classLabel: String
) {
    FECA("icon/symbol_1.png", "banner/banner_1.png", "simple_icon/10.png", "Feca"),
    OSAMODAS("icon/symbol_2.png", "banner/banner_2.png", "simple_icon/20.png", "Osamodas"),
    ENUTROF("icon/symbol_3.png", "banner/banner_3.png", "simple_icon/30.png", "Enutrof"),
    SRAM("icon/symbol_4.png", "banner/banner_4.png", "simple_icon/40.png", "Sram"),
    XELOR("icon/symbol_5.png", "banner/banner_5.png", "simple_icon/50.png", "Xelor"),
    ECAFLIP("icon/symbol_6.png", "banner/banner_6.png", "simple_icon/60.png", "Ecaflip"),
    ENIRIPSA("icon/symbol_7.png", "banner/banner_7.png", "simple_icon/70.png", "Eniripsa"),
    IOP("icon/symbol_8.png", "banner/banner_8.png", "simple_icon/80.png", "Iop"),
    CRA("icon/symbol_9.png", "banner/banner_9.png", "simple_icon/90.png", "Cra"),
    SADIDA("icon/symbol_10.png", "banner/banner_10.png", "simple_icon/100.png", "Sadida"),
    SACRIEUR("icon/symbol_11.png", "banner/banner_11.png", "simple_icon/110.png", "Sacrieur"),
    PANDAWA("icon/symbol_12.png", "banner/banner_12.png", "simple_icon/120.png", "Pandawa"),
    ROUBLARD("icon/symbol_13.png", "banner/banner_13.png", "simple_icon/130.png", "Roublard"),
    ZOBAL("icon/symbol_14.png", "banner/banner_14.png", "simple_icon/140.png", "Zobal"),
    STEAMER("icon/symbol_15.png", "banner/banner_15.png", "simple_icon/150.png", "Steamer"),
    ELIOTROPE("icon/symbol_16.png", "banner/banner_16.png", "simple_icon/160.png", "Eliotrope"),
    HUPPERMAGE("icon/symbol_17.png", "banner/banner_17.png", "simple_icon/170.png", "Huppermage"),
    OUGINAK("icon/symbol_18.png", "banner/banner_18.png", "simple_icon/180.png", "Ouginak");

    private val basePath = "/icon/classes/"
    val iconUrl: URL = javaClass.getResource(basePath + iconFileName) ?: error("Couldn't find icon [$iconFileName]")
    val bannerUrl: URL =
        javaClass.getResource(basePath + bannerFileName) ?: error("Couldn't find banner [$bannerFileName]")
    val simpleIconUrl: URL =
        javaClass.getResource(basePath + simpleIconFileName) ?: error("Couldn't find simple icon [$simpleIconFileName]")

}