package fr.lewon.dofus.bot.util.external.metamob

import fr.lewon.dofus.bot.util.external.AbstractRequestProcessor
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonsterUpdate
import fr.lewon.dofus.bot.util.filemanagers.impl.MetamobConfigManager
import java.awt.image.BufferedImage
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO

object MetamobRequestProcessor : AbstractRequestProcessor("https://api.metamob.fr") {

    private const val USERS = "/utilisateurs/"
    private const val MONSTERS = "/monstres/"

    @Synchronized
    fun getAllMonsters(): List<MetamobMonster>? {
        return getForObject("$USERS${getMetamobUsername()}$MONSTERS")
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
        putForObject<HashMap<*, *>>("$USERS${getMetamobUsername()}$MONSTERS", monsterUpdates)
    }

    @Synchronized
    override fun checkParameters(): Boolean {
        val co = buildConnection("$USERS${getMetamobUsername()}$MONSTERS")
        try {
            co.requestMethod = "PUT"
            writeBody(Object(), co)
            if (co.responseCode != 200) {
                println("REQUEST ERROR : ${co.responseMessage}")
            }
            return co.responseCode == 200
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun setRequestProperties(co: HttpURLConnection) {
        co.setRequestProperty("HTTP-X-APIKEY", "59f661-004e81-cf6352-478f78-01f8df")
        co.setRequestProperty("HTTP-X-USERKEY", getMetamobUniqueId())
    }

    private fun getMetamobUniqueId(): String {
        return MetamobConfigManager.readConfig().metamobUniqueID ?: ""
    }

    private fun getMetamobUsername(): String {
        return MetamobConfigManager.readConfig().metamobUsername ?: ""
    }

}