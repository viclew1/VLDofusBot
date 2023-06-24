package fr.lewon.dofus.bot.core.d2o.managers.characteristic

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.i18n.I18NUtil
import fr.lewon.dofus.bot.core.model.charac.DofusBreed

object BreedManager : VldbManager {

    const val anyBreedId = 19
    private lateinit var commonBreed: DofusBreed
    private lateinit var breedById: Map<Int, DofusBreed>

    override fun initManager() {
        breedById = D2OUtil.getObjects("Breeds").associate {
            val id = it["id"].toString().toInt()
            val nameId = it["shortNameId"].toString().toInt()
            id to DofusBreed(id, I18NUtil.getLabel(nameId) ?: "UNKNOWN_BREED_NAME")
        }
        commonBreed = DofusBreed(anyBreedId, "COMMON_BREED")
    }

    override fun getNeededManagers(): List<VldbManager> {
        return emptyList()
    }

    fun getBreed(id: Int): DofusBreed {
        return breedById[id] ?: error("No breed for id : $id")
    }

    fun getAllBreeds(includeCommonBreed: Boolean = false): List<DofusBreed> {
        val breeds = breedById.values.toMutableList()
        if (includeCommonBreed) {
            breeds.add(commonBreed)
        }
        return breeds
    }

}
