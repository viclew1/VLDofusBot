package fr.lewon.dofus.bot.sniffer.game.chat

enum class DofusChannel {
    UNKNOWN,
    GENERAL,
    TEAM,
    PRIVATE,
    BUSINESS,
    RECRUITMENT;


    companion object {
        fun fromByte(data: Byte): DofusChannel {
            return when (data.toInt()) {
                0 -> GENERAL
                0x04 -> TEAM
                0x09 -> PRIVATE
                0x05 -> BUSINESS
                0x06 -> RECRUITMENT
                else -> UNKNOWN
            }
        }
    }
}