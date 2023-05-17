package fr.lewon.dofus.bot.core.d2o.gamedata

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader

class GameDataClassDefinition {

    private val fields = ArrayList<GameDataField>()

    fun read(module: String, stream: ByteArrayReader): Map<String, Any> {
        val content = HashMap<String, Any>()
        for (field in fields) {
            content[field.name] = field.readData(module, stream, 0)
        }
        return content
    }

    fun addField(fieldName: String, stream: ByteArrayReader) {
        val field = GameDataField(fieldName)
        field.readType(stream)
        this.fields.add(field)
    }

}