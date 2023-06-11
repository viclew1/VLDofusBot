package fr.lewon.dofus.bot.util.external.metamob

import fr.lewon.dofus.bot.util.external.AbstractRequestProcessor
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonsterUpdate
import fr.lewon.dofus.bot.util.filemanagers.impl.MetamobConfigManager
import java.net.HttpURLConnection
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

object MetamobRequestProcessor : AbstractRequestProcessor("https://api.metamob.fr") {

    private const val USERS = "/utilisateurs/"
    private const val MONSTERS = "/monstres/"

    @Synchronized
    fun getAllMonsters(): List<MetamobMonster>? {
        return getForObject("$USERS${getMetamobUsername()}$MONSTERS")
    }

    @Synchronized
    fun updateMonsters(monsterUpdates: List<MetamobMonsterUpdate>) {
        putForObject<HashMap<*, *>>("$USERS${getMetamobUsername()}$MONSTERS", monsterUpdates)
    }

    override fun buildConnection(uri: String): HttpURLConnection =
        super.buildConnection(uri).also { co ->
            if (co is HttpsURLConnection) {
                co.hostnameVerifier = HostnameVerifier { _, _ -> true }
                co.sslSocketFactory = SSLContext.getInstance("SSL").also { context ->
                    val trustManager = object : X509TrustManager {
                        override fun checkClientTrusted(certs: Array<out X509Certificate>, authType: String) {}
                        override fun checkServerTrusted(certs: Array<out X509Certificate>, authType: String) {}
                        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
                    }
                    context.init(null, arrayOf(trustManager), SecureRandom())
                }.socketFactory
            }
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