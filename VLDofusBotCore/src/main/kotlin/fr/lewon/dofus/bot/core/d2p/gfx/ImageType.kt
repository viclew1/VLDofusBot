package fr.lewon.dofus.bot.core.d2p.gfx

enum class ImageType {

    PNG,
    JPG,
    SWF;

    companion object {
        fun fromStr(str: String): ImageType {
            return values().firstOrNull { it.name.lowercase() == str.lowercase() }
                ?: error("Invalid Image type : $str")
        }
    }

}