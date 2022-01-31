package fr.lewon.dofus.bot.util.filemanagers

import fr.lewon.dofus.bot.core.d2o.managers.characteristic.BreedManager
import fr.lewon.dofus.bot.model.characters.DofusBreedAssets

object BreedAssetManager {

    private lateinit var breedAssetsById: Map<Int, DofusBreedAssets>

    fun initManager() {
        breedAssetsById = BreedManager.getAllBreeds().associate { it.id to DofusBreedAssets(it) }
    }

    fun getAllAssets(): List<DofusBreedAssets> {
        return breedAssetsById.values.sortedBy { it.getIndex() }
    }

    fun getAssets(classId: Int): DofusBreedAssets {
        return breedAssetsById[classId] ?: error("No dofus class for id : $classId")
    }

}