package fr.lewon.dofus.bot.core.ui.dat.amf3

enum class AMF3Type(private val id: Int) {

    NULL(0x01),
    BOOL_FALSE(0x02),
    BOOL_TRUE(0x03),
    INTEGER(0x04),
    NUMBER(0x05),
    STRING(0x06),
    DATE(0x08),
    ARRAY(0x09),
    OBJECT(0x0A),
    BYTE_ARRAY(0x0C);

    companion object {
        fun fromInt(id: Int): AMF3Type {
            return values().firstOrNull { it.id == id }
                ?: error("No Amf3Type for id : $id")
        }
    }

}