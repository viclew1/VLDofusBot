package fr.lewon.dofus.bot.util.external

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import java.net.HttpURLConnection
import java.net.URL

abstract class AbstractRequestProcessor(private val baseUri: String) {

    protected val objectMapper: ObjectMapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)

    @Synchronized
    open fun checkParameters(): Boolean {
        return true
    }

    protected inline fun <reified T> putForObject(uri: String, body: Any): T? {
        if (!checkParameters()) {
            return null
        }
        val co = buildConnection(uri)
        co.requestMethod = "PUT"
        writeBody(body, co)
        return readResponse(co)
    }

    protected inline fun <reified T> postForObject(uri: String, body: Any): T? {
        if (!checkParameters()) {
            return null
        }
        val co = buildConnection(uri)
        co.requestMethod = "POST"
        writeBody(body, co)
        return readResponse(co)
    }

    protected inline fun <reified T> getForObject(uri: String): T? {
        return get(uri)?.let { objectMapper.readValue(it) }
    }

    protected fun get(uri: String): ByteArray? {
        return takeIf { checkParameters() }?.buildConnection(uri)?.run {
            requestMethod = "GET"
            responseCode
            inputStream.readAllBytes()
        }
    }

    protected fun buildConnection(uri: String): HttpURLConnection {
        val slash = if (!baseUri.endsWith("/") && !uri.startsWith("/")) "/" else ""
        return (URL("${this.baseUri}$slash$uri").openConnection() as HttpURLConnection)
            .also { setRequestProperties(it) }
    }

    protected abstract fun setRequestProperties(co: HttpURLConnection)

    protected fun writeBody(bodyObj: Any, connection: HttpURLConnection) {
        val bodyJson = objectMapper.writeValueAsString(bodyObj)
        connection.doOutput = true
        val os = connection.outputStream
        os.write(bodyJson.toByteArray(Charsets.UTF_8), 0, bodyJson.length)
    }

    protected inline fun <reified T> readResponse(connection: HttpURLConnection): T {
        connection.responseCode
        return objectMapper.readValue(connection.inputStream.readAllBytes())
    }
}