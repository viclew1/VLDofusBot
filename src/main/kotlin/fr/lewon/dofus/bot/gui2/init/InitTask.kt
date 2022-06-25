package fr.lewon.dofus.bot.gui2.init

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class InitTask(
    val label: String,
    val executed: MutableState<Boolean> = mutableStateOf(false),
    val success: MutableState<Boolean> = mutableStateOf(false),
    val executing: MutableState<Boolean> = mutableStateOf(false),
    val function: () -> Unit
)