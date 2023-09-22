package fr.lewon.dofus.bot.gui.main.jobs

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.model.jobs.HarvestJobs
import fr.lewon.dofus.bot.util.filemanagers.impl.HarvestableSetsManager

object JobsUiUtil : ComposeUIUtil() {

    val selectedSetName: MutableState<String?> = mutableStateOf(null)
    val harvestableIdsBySetName = mutableStateOf(HarvestableSetsManager.getHarvestableIdsBySetName())

    override fun init() {
        HarvestJobs.entries.flatMap { it.items }.forEach { it.cachedIcon }
    }

    fun deleteSet(setName: String) {
        HarvestableSetsManager.deleteSet(setName)
        harvestableIdsBySetName.value = HarvestableSetsManager.getHarvestableIdsBySetName()
    }

}