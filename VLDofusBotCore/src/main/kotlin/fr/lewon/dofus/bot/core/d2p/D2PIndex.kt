package fr.lewon.dofus.bot.core.d2p

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader

class D2PIndex(
    val key: String,
    val offset: Int,
    val length: Int,
    val filePath: String,
    val stream: ByteArrayReader? = null
)