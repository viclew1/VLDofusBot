package fr.lewon.dofus.bot.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.json.Hint
import fr.lewon.dofus.bot.json.MoveHints
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient

object DTBRequestProcessor {

    private val webClient = WebClient.builder()
        .codecs { c -> c.defaultCodecs().maxInMemorySize(-1) }
        .baseUrl("https://dofus-map.com/")
        .build()
    private val objectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    private val cookieStore = LinkedMultiValueMap<String, String>()

    fun getAllHints(): MutableMap<String, List<String>> {
        cookieStore.clear()
        cookieStore["_ga"] = "GA1.2.98910942.1586533380"
        cookieStore["_gid"] = "GA1.2.1173554551.1586533380"
        cookieStore["language"] = "fr"
        val content = readAllPage("/hunt")

        val allElementsJson = Regex("var text = (\\{.*?});")
            .find(content)
            ?.destructured
            ?.component1()
            ?: throw Exception("Unable to find hints")

        val allHintsNameIdsJson = Regex("var allHintsNameIds = (\\[.*?]);")
            .find(content)
            ?.destructured
            ?.component1()
            ?: throw Exception("Unable to find hints")

        val allHintsNameIds = objectMapper.readValue<Array<String>>(allHintsNameIdsJson)

        return objectMapper.readValue<Map<String, String>>(allElementsJson)
            .filter { allHintsNameIds.contains(it.key) }
            .map { it.key to reduceStr(it.value) }
            .groupBy { it.second }
            .mapValuesTo(HashMap()) { it.value.map { pair -> pair.first } }
    }

    private fun reduceStr(str: String, length: Int = 31): String {
        if (str.length <= length) return str
        return str.substring(0, length)
    }

    fun getHint(x: Int, y: Int, direction: Directions, toFindId: List<String>, world: String): Hint? {
        return getForBody<MoveHints>("/huntTool/getData.php?x=$x&y=$y&direction=${direction.name.toLowerCase()}&world=$world&language=fr")
            ?.hints
            ?.firstOrNull { toFindId.contains(it.n) }
    }

    private fun readAllPage(uri: String): String {
        return getForBody(uri) ?: ""
    }

    @Synchronized
    private fun <T> getForBody(uri: String, responseType: Class<T>): T? {
        val response = webClient.get()
            .uri(uri)
            .header("cookie", cookieStore.entries.joinToString("; ") { (k, v) -> "$k=${v[0]}" })
            .exchange().block()
        response?.cookies()?.forEach { (n, u) -> cookieStore[n] = u[0].value }
        return response?.bodyToMono(responseType)?.block()
    }

    private inline fun <reified T> getForBody(uri: String): T? {
        return getForBody(uri, T::class.java)
    }
}