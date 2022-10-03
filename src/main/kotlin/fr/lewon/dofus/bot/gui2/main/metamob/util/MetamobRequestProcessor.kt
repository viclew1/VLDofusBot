package fr.lewon.dofus.bot.gui2.main.metamob.util

import fr.lewon.dofus.bot.gui2.main.metamob.model.MetamobMonster
import fr.lewon.dofus.bot.gui2.main.metamob.model.MetamobMonsterUpdate
import fr.lewon.dofus.bot.util.filemanagers.impl.MetamobConfigManager
import fr.lewon.dofus.bot.util.http.AbstractRequestProcessor
import java.awt.image.BufferedImage
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO

object MetamobRequestProcessor : AbstractRequestProcessor("https://api-metamob.fr") {

    private const val USERS = "/utilisateurs/"
    private const val MONSTERS = "/monstres/"

    @Synchronized
    fun getAllMonsters(): List<MetamobMonster>? {
        return getForObject("$USERS${getMetamobPseudo()}$MONSTERS")
    }

    @Synchronized
    fun getImage(imageUrl: String): BufferedImage? {
        return try {
            ImageIO.read(URL(imageUrl))
        } catch (t: Throwable) {
            null
        }
    }

    @Synchronized
    fun updateMonsters(monsterUpdates: List<MetamobMonsterUpdate>) {
        putForObject<HashMap<*, *>>("$USERS${getMetamobPseudo()}$MONSTERS", monsterUpdates)
    }

    @Synchronized
    override fun checkParameters(): Boolean {
        val co = buildConnection("$USERS${getMetamobPseudo()}$MONSTERS")
        co.requestMethod = "PUT"
        writeBody(Object(), co)
        if (co.responseCode != 200) {
            println("REQUEST ERROR : ${co.responseMessage}")
        }
        return co.responseCode == 200
    }

    override fun setRequestProperties(co: HttpURLConnection) {
        co.setRequestProperty("HTTP-X-APIKEY", "59f661-004e81-cf6352-478f78-01f8df")
        co.setRequestProperty("HTTP-X-USERKEY", MetamobConfigManager.readConfig().metamobUniqueID)
    }

    private fun getMetamobPseudo(): String {
        return MetamobConfigManager.readConfig().metamobPseudo ?: ""
    }

}