package fr.lewon.dofus.bot.gui.main.devtools

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateOf
import com.fasterxml.jackson.databind.ObjectMapper
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.i18n.I18NUtil
import fr.lewon.dofus.bot.gui.ComposeUIUtil

object DevToolsUiUtil : ComposeUIUtil() {

    private val uiState = mutableStateOf(DevToolsUiState())

    fun getUiStateValue() = uiState.value

    fun selectD2OModule(d2oModuleName: String) {
        uiState.value = uiState.value.copy(loading = true)
        val mapper = ObjectMapper()
        uiState.value = uiState.value.copy(
            selectedD2OModule = d2oModuleName,
            loading = false,
            selectedModuleListState = LazyListState(),
            selectedModuleItems = D2OUtil.getObjects(d2oModuleName).map { item ->
                val id = item["id"]?.toString()?.toDoubleOrNull()?.toLong() ?: 0L
                val nameId = item["nameId"]?.toString()?.toInt()
                val name = nameId?.let { I18NUtil.getLabel(nameId) }
                val descriptionId = item["descriptionId"]?.toString()?.toInt()
                val description = descriptionId?.let { I18NUtil.getLabel(descriptionId) }
                val content = mapper.writeValueAsString(item)
                    .replace(",", ", ")
                    .replace(",  ", ", ")
                ModuleItem(id, name, description, content)
            }.sortedBy { it.id },
            moduleItemNameFilter = "",
            moduleItemIdFilter = "",
        )
    }

    fun updateNameFilter(nameFilter: String) {
        uiState.value = uiState.value.copy(nameFilter = nameFilter)
    }

    fun updateModuleItemNameFilter(moduleItemNameFilter: String) {
        uiState.value = uiState.value.copy(moduleItemNameFilter = moduleItemNameFilter)
    }

    fun updateModuleItemIdFilter(moduleItemIdFilter: String) {
        uiState.value = uiState.value.copy(moduleItemIdFilter = moduleItemIdFilter)
    }

}