package fr.lewon.dofus.bot.core.d2p

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader

abstract class AbstractD2PUrlLoaderAdapter(protected val loaderHeader: Int) {

    abstract fun initStream(path: String)

    protected fun loadFromData(data: ByteArray): ByteArrayReader {
        var stream = ByteArrayReader(data)
        var header = stream.readByte().toInt()
        if (header != loaderHeader) {
            stream.setPosition(0)
            stream = ByteArrayReader(stream.uncompress())

            header = stream.readByte().toInt()
            if (header != loaderHeader) {
                error("Invalid D2P stream, expected loaderHeader : $loaderHeader, actual : $header")
            }
        }
        stream.setPosition(0)
        return stream
    }

}