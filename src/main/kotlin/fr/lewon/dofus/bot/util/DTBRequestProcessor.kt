package fr.lewon.dofus.bot.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.game.move.Direction
import fr.lewon.dofus.bot.model.hint.MoveHints
import java.net.HttpURLConnection
import java.net.URL

object DTBRequestProcessor {

    private val objectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun getAllHintIdsByName(): HashMap<String, Int> {
        val content: String = getForResponseBody("/hunt")

        val allElementsJson = findFirstInRegex("var text = (\\{.*?});", content)
        val allHintsNameIdsJson = findFirstInRegex("var allHintsNameIds = (\\[.*?]);", content)
        val allHintsNameIds = objectMapper.readValue<Array<Int>>(allHintsNameIdsJson)

        return objectMapper.readValue<Map<Int, String>>(allElementsJson)
            .filter { allHintsNameIds.contains(it.key) }
            .entries.associate { (key, value) -> value to key }
            .toMap(HashMap())
    }

    private fun findFirstInRegex(pattern: String, content: String): String {
        return Regex(pattern)
            .find(content)
            ?.destructured
            ?.component1()
            ?: throw Exception("Unable to find hints")
    }

    fun getHints(x: Int, y: Int, direction: Direction, world: Int): MoveHints {
        val w = if (world == 1) 0 else 2
        return getForObject("/huntTool/getData.php?x=$x&y=$y&direction=${direction.name.toLowerCase()}&world=$w&language=fr")
    }

    @Synchronized
    private fun getForResponseBody(uri: String): String {
        val co = URL("https://dofus-map.com/$uri").openConnection() as HttpURLConnection
        co.requestMethod = "GET"
        co.responseCode
        return String(co.inputStream.readAllBytes())
    }

    private inline fun <reified T> getForObject(uri: String): T {
        val responseBody = getForResponseBody(uri)
        return objectMapper.readValue(responseBody)
    }
}