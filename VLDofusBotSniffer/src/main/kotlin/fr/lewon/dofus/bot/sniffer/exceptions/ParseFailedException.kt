package fr.lewon.dofus.bot.sniffer.exceptions

class ParseFailedException(messageName: String, messageId: Int, cause: Throwable) :
    IllegalStateException("Couldn't parse message $messageName:$messageId", cause)