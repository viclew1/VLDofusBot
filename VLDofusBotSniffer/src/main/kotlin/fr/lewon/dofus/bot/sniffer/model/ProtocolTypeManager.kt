package fr.lewon.dofus.bot.sniffer.model

import fr.lewon.dofus.bot.sniffer.managers.TypeIdByName
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import org.reflections.Reflections


object ProtocolTypeManager {

    private val typeByName = HashMap<String, Class<out NetworkType>>()

    init {
        Reflections(javaClass.packageName).getSubTypesOf(NetworkType::class.java).forEach {
            typeByName[it.simpleName] = it
        }
    }

    fun <T : NetworkType> getInstance(id: Int): T {
        val scriptName = TypeIdByName.getName(id) ?: error("Couldn't find name for id [$id]")
        val refClass = typeByName[scriptName] ?: error("No script found for name [$scriptName]")
        return refClass.getConstructor().newInstance() as T
    }

}