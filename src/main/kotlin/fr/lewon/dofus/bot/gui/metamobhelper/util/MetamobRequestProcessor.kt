package fr.lewon.dofus.bot.gui.metamobhelper.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.gui.metamobhelper.model.MetamobMonster
import fr.lewon.dofus.bot.gui.metamobhelper.model.MetamobMonsterUpdate
import fr.lewon.dofus.bot.util.filemanagers.impl.MetamobConfigManager
import java.awt.image.BufferedImage
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO

object MetamobRequestProcessor {

    private const val BASE_URI = "https://api.metamob.fr/"
    private const val USERS = "/utilisateurs/"
    private const val MONSTERS = "/monstres/"

    private val objectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)

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
    fun checkParameters(): Boolean {
        val co = buildConnection("$USERS${getMetamobPseudo()}$MONSTERS")
        co.requestMethod = "PUT"
        writeBody(Object(), co)
        if (co.responseCode != 200) {
            println("REQUEST ERROR : ${co.responseMessage}")
        }
        return co.responseCode == 200
    }

    private inline fun <reified T> putForObject(uri: String, body: Any): T? {
        if (!checkParameters()) {
            return null
        }
        val co = buildConnection(uri)
        co.requestMethod = "PUT"
        writeBody(body, co)
        return readResponse(co)
    }

    private fun buildConnection(uri: String): HttpURLConnection {
        val co = URL("$BASE_URI$uri").openConnection() as HttpURLConnection
        co.setRequestProperty("HTTP-X-APIKEY", "59f661-004e81-cf6352-478f78-01f8df")
        co.setRequestProperty("HTTP-X-USERKEY", MetamobConfigManager.readConfig().metamobUniqueID)
        return co
    }

    private fun getMetamobPseudo(): String {
        return MetamobConfigManager.readConfig().metamobPseudo ?: ""
    }

    private fun writeBody(bodyObj: Any, connection: HttpURLConnection) {
        val bodyJson = objectMapper.writeValueAsString(bodyObj)
        connection.doOutput = true
        val os = connection.outputStream
        os.write(bodyJson.toByteArray(Charsets.UTF_8), 0, bodyJson.length)
    }

    private inline fun <reified T> readResponse(connection: HttpURLConnection): T {
        connection.responseCode
        return objectMapper.readValue(connection.inputStream.readAllBytes())
    }

    private inline fun <reified T> getForObject(uri: String): T? {
        if (!checkParameters()) {
            return null
        }
        val co = buildConnection(uri)
        co.requestMethod = "GET"
        return readResponse(co)
    }
}