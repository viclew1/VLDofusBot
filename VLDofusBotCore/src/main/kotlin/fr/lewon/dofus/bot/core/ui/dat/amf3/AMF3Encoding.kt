package fr.lewon.dofus.bot.core.ui.dat.amf3

enum class AMF3Encoding(private val id: Int) {
    STATIC(0x00),
    EXTERNAL(0x01),
    DYNAMIC(0x02);

    companion object {
        fun fromInt(id: Int): AMF3Encoding {
            return values().firstOrNull { it.id == id }
                ?: error("No AMF3Encoding for id : $id")
        }
    }

}