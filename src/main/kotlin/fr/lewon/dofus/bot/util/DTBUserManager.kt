package fr.lewon.dofus.bot.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.json.UserData
import fr.lewon.dofus.bot.json.UserDataStore
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object DTBUserManager {

    private val userDataStore: UserDataStore
    private val dataStoreFile: File = File("config/user_data")
    private val mapper = ObjectMapper()

    init {
        val module = SimpleModule()
        mapper.registerModule(module)
        if (dataStoreFile.exists()) {
            userDataStore = ObjectMapper().readValue(dataStoreFile)
        } else {
            userDataStore = UserDataStore()
            saveUserData()
        }
    }

    fun saveUserData() {
        with(OutputStreamWriter(FileOutputStream(dataStoreFile, false), StandardCharsets.UTF_8)) {
            write(ObjectMapper().writeValueAsString(userDataStore))
            close()
        }
    }

    fun setCurrentUser(userData: UserData) {
        userDataStore.currentUser = userData.login
    }

    fun getCurrentUser(): UserData? {
        val currentUser = userDataStore.currentUser ?: return null
        return userDataStore.users.firstOrNull { it.login == currentUser }
    }

    fun userExists(userName: String): Boolean {
        return userDataStore.users.firstOrNull { it.login == userName } != null
    }

    fun getUser(userName: String): UserData {
        return userDataStore.users.firstOrNull { it.login == userName } ?: error("User [$userName] does not exist")
    }

    fun getUserNames(): List<String> {
        return userDataStore.users.map { it.login }
    }

    fun addUser(userName: String): UserData {
        userDataStore.users.firstOrNull { it.login == userName }?.let {
            error("UserName already registered")
        }
        val newUser = UserData(userName)
        userDataStore.users.add(newUser)
        saveUserData()
        return newUser
    }

    fun removeUser(userData: UserData) {
        userDataStore.users.remove(userData)
        saveUserData()
    }
}
