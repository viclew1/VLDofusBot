package fr.lewon.dofus.bot.gui.panes.script.selector

import fr.lewon.dofus.bot.gui.ResourceConsumingPanel
import fr.lewon.dofus.bot.gui.panes.script.StatsPanel
import fr.lewon.dofus.bot.gui.util.AppFonts
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.util.FormatUtil
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.script.ScriptRunner
import net.miginfocom.swing.MigLayout
import java.awt.Color
import java.util.*
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.LineBorder

class CharacterScriptSelectorPanel(private val character: DofusCharacter) : AbstractScriptSelectorPanel(),
    ResourceConsumingPanel {

    private var scriptStartMillis = -1L
    private var updateUITimer: Timer? = null
    private val scriptTimerPanel = JPanel(MigLayout()).also { it.border = LineBorder(Color.BLACK, 1) }
    private val timerLabel = JLabel()
    private val statsLabel = JLabel("Stats").also { it.font = AppFonts.TITLE_FONT }
    private val statsPanel = StatsPanel()

    init {
        ScriptRunner.addListener(character, this)
        updateTimerLabel()
    }

    private fun updateTimerLabel(runningScript: DofusBotScript? = null) {
        val durationMillis = if (scriptStartMillis > 0) System.currentTimeMillis() - scriptStartMillis else 0
        timerLabel.text = FormatUtil.durationToStr(durationMillis)
        runningScript?.let { timerLabel.text += " - ${it.name}" }
    }

    override fun addScriptLauncherPane() {
        super.addScriptLauncherPane()
        scriptTimerPanel.add(JLabel("Time : "))
        scriptTimerPanel.add(timerLabel)
        add(scriptTimerPanel, "span 3 1, height 25, width max, wrap")
    }

    override fun addPanes() {
        super.addPanes()
        if (isRunning()) {
            addStatsPanes()
            val runningScript = ScriptRunner.getRunningScript(character)
                ?: error("There should be a running script")
            startStatsUpdate(runningScript.script, runningScript.startTime)
        }
    }

    private fun startStatsUpdate(script: DofusBotScript, scriptStartMillis: Long = System.currentTimeMillis()) {
        this.scriptStartMillis = scriptStartMillis
        updateExecUI(script)
        updateUITimer = Timer()
        updateUITimer?.schedule(buildUpdateExecUITimerTask(script), 0, 1000)
    }

    private fun buildUpdateExecUITimerTask(script: DofusBotScript): TimerTask {
        return object : TimerTask() {
            override fun run() {
                updateExecUI(script)
            }
        }
    }

    private fun updateExecUI(script: DofusBotScript) {
        updateTimerLabel(script)
        statsPanel.updateScriptStats(script)
    }

    private fun stopStatsUpdate() {
        updateUITimer?.cancel()
        updateUITimer = null
        scriptStartMillis = -1
    }

    override fun onScriptEnd() {
        stopStatsUpdate()
    }

    override fun onScriptStart(script: DofusBotScript) {
        if (script.getStats().isNotEmpty() && !components.contains(statsPanel)) {
            addStatsPanes()
        } else if (script.getStats().isEmpty() && components.contains(statsPanel)) {
            remove(statsLabel)
            remove(statsPanel)
        }
        super.updateUI()
        startStatsUpdate(script)
    }

    private fun addStatsPanes() {
        add(statsLabel, "wrap")
        add(statsPanel, "span 3 1, h 140:140:140, width max, wrap")
    }

    override fun isRunning(): Boolean {
        return ScriptRunner.isScriptRunning(character)
    }

    override fun getInitialParameterValue(parameter: DofusBotParameter, script: DofusBotScript): String {
        return CharacterManager.getParamValue(character, script, parameter) ?: parameter.defaultValue
    }

    override fun onParamUpdate(script: DofusBotScript, param: DofusBotParameter) {
        CharacterManager.updateParamValue(character, script, param)
    }

    override fun runScript(script: DofusBotScript) {
        ScriptRunner.runScript(character, script)
    }

    override fun stopScript() {
        ScriptRunner.stopScript(character)
    }

    override fun scriptEnded(character: DofusCharacter): Boolean {
        return true
    }

    override fun scriptStarted(character: DofusCharacter): Boolean {
        return true
    }

    override fun stopAll() {
        stopStatsUpdate()
    }
}