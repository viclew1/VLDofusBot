package fr.lewon.dofus.bot.util.filemanagers.impl

import fr.lewon.dofus.bot.core.d2o.managers.characteristic.BreedManager
import fr.lewon.dofus.bot.model.characters.DofusBreedAssets
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager

object BreedAssetManager : ToInitManager {

    private lateinit var breedAssetsById: Map<Int, DofusBreedAssets>

    override fun initManager() {
        breedAssetsById = BreedManager.getAllBreeds().associate { it.id to DofusBreedAssets(it) }
    }

    override fun getNeededManagers(): List<ToInitManager> {
        return emptyList()
    }

    fun getAllAssets(): List<DofusBreedAssets> {
        return breedAssetsById.values.sortedBy { it.breed.id }
    }

    fun getAssets(classId: Int): DofusBreedAssets {
        return breedAssetsById[classId] ?: error("No dofus class for id : $classId")
    }

}