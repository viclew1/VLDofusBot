package fr.lewon.dofus.bot.gui2.init

import fr.lewon.dofus.export.builder.VldbRegexExportPackTaskBuilder
import java.util.regex.Matcher

object DecryptionKeyExportPackTaskBuilder :
    VldbRegexExportPackTaskBuilder("Map", "decryptionKey\\.writeMultiByte\\(\"(.*?)\"\\,\"(.*?)\"\\);") {

    lateinit var decryptionKey: String
    lateinit var decryptionKeyCharset: String

    override fun treatMatcher(matcher: Matcher) {
        decryptionKey = matcher.group(1)
        decryptionKeyCharset = matcher.group(2)
    }

}