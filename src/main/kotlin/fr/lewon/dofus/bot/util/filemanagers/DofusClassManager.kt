package fr.lewon.dofus.bot.util.filemanagers

import fr.lewon.dofus.bot.core.d2o.managers.characteristic.BreedManager
import fr.lewon.dofus.bot.model.characters.DofusClass

object DofusClassManager {

    private lateinit var classById: Map<Int, DofusClass>

    fun initManager() {
        classById = BreedManager.getAllBreeds().associate { it.id to DofusClass(it) }
    }

    fun getAllClasses(): List<DofusClass> {
        return classById.values.sortedBy { it.getIndex() }
    }

    fun getClass(classId: Int): DofusClass {
        return classById[classId] ?: error("No dofus class for id : $classId")
    }

}