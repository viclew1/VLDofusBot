package fr.lewon.dofus.bot.json

import com.fasterxml.jackson.annotation.JsonProperty
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter

class UserDataStore(
    @field:JsonProperty var users: MutableList<UserData> = ArrayList(),
    @field:JsonProperty var currentUser: String? = null
)

class UserData(
    @field:JsonProperty var login: String = "",
    @field:JsonProperty var password: String = "",
    @field:JsonProperty var huntLevel: Int = 200,
    @field:JsonProperty var scriptParameters: HashMap<String, ArrayList<DofusBotScriptParameter>> = HashMap()
)