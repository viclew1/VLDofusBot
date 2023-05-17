package fr.lewon.dofus.bot.sniffer.exceptions

class MessageIdNotFoundException(messageId: Int) : IllegalStateException("Message ID not found : $messageId")