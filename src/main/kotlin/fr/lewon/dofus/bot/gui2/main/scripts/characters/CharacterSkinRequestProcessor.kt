package fr.lewon.dofus.bot.gui2.main.scripts.characters

import fr.lewon.dofus.bot.util.http.AbstractRequestProcessor
import java.net.HttpURLConnection

object CharacterSkinRequestProcessor : AbstractRequestProcessor("https://www.dofusbook.net") {

    private const val SKINATOR = "/fr/outils/skinator"

    override fun setRequestProperties(co: HttpURLConnection) {
        // Nothing
    }


}

fun main() {
    val indexedColors = listOf(
        18777487,
        48357858,
        52052826,
        68053361,
        93462581,
    )
    for (uIndexedColor in indexedColors) {
        println("----")
        val uIndex = (uIndexedColor shr 24) - 1
        val uColor = uIndexedColor and 16777215
        println(uIndex)
        println(uColor)
        println(Integer.toHexString(uColor))
    }
    println(indexedColors.map {
        val uIndex = (it shr 24) - 1
        val uColor = it and 16777215
        Integer.toHexString(uColor)
    })
}