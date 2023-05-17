package fr.lewon.dofus.bot.sniffer.exceptions

class AddToStoreFailedException(messageName: String, cause: Throwable) :
    IllegalStateException("Couldn't add message [$messageName] to store", cause)