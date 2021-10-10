package fr.lewon.dofus.bot.gui.init

import javax.swing.JLabel
import javax.swing.JProgressBar

class InitTask(val label: JLabel, val progressBar: JProgressBar, val function: () -> Unit, var success: Boolean = false)