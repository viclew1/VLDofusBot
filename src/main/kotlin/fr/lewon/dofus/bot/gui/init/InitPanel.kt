package fr.lewon.dofus.bot.gui.init

import fr.lewon.dofus.bot.VLDofusBot
import fr.lewon.dofus.bot.core.VldbCoreInitializer
import fr.lewon.dofus.bot.gui.InitFrame
import fr.lewon.dofus.bot.sniffer.DofusMessageReceiverUtil
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.filemanagers.BreedAssetManager
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import fr.lewon.dofus.bot.util.filemanagers.HintManager
import net.miginfocom.swing.MigLayout
import org.reflections.Reflections
import java.awt.Color
import javax.swing.*


object InitPanel : JPanel(MigLayout("ins 10")) {

    private var errorOnInit = true

    private val initTasks = listOf(
        buildInitTask("Dofus decompiled") { DofusMessageReceiverUtil.prepareNetworkManagers() },
        buildInitTask("VLDofusBotCore") { VldbCoreInitializer.initAll() },
        buildInitTask("File managers") { initFileManagers() },
        buildInitTask("Sniffer handlers") { initEventStoreHandlers() },
    )

    private val resultLabel = JTextArea().also {
        it.lineWrap = true
        it.isEditable = false
        it.isVisible = false
    }
    private val retryButton = JButton("Retry").also {
        it.isVisible = false
        it.addActionListener { Thread { InitFrame.startInit() }.start() }
    }

    init {
        background = Color.DARK_GRAY
        resultLabel.background = background
        initTasks.forEach { addLine(it.label, it.progressBar, it != initTasks.last()) }

        add(JPanel().also { it.background = background }, "span 2, grow, pushy, wrap")
        val retryButtonPanel = JPanel(MigLayout("", "[center, grow]"))
        val resultLabelPanel = JPanel(MigLayout("", "[fill, center, grow]"))
        retryButtonPanel.background = background
        resultLabelPanel.background = background
        retryButtonPanel.add(retryButton)
        resultLabelPanel.add(resultLabel)
        add(retryButtonPanel, "dock south")
        add(resultLabelPanel, "dock south")
    }

    private fun buildInitTask(labelStr: String, function: () -> Unit): InitTask {
        return InitTask(JLabel(labelStr), JProgressBar(), function)
    }

    private fun addLine(leftComponent: JComponent, rightComponent: JComponent, separator: Boolean = true) {
        add(leftComponent)
        add(rightComponent, "width 60, height 8, al right, wrap")
        if (separator) add(JSeparator(JSeparator.HORIZONTAL), "span 2 1, width max, wrap")
    }

    fun initAll(): Boolean {
        super.updateUI()
        val toInitTasks = initTasks.filter { !it.success }
        retryButton.isVisible = false
        resultLabel.isVisible = false
        val errors = ArrayList<String>()
        toInitTasks.forEach { prepareInit(it) }
        toInitTasks.forEach { startInit(it, errors) }
        val success = initTasks.none { !it.success }
        errorOnInit = false
        resultLabel.isVisible = true
        if (success) {
            resultLabel.text = "VLDofusBot initialization OK !"
            resultLabel.foreground = Color.GREEN
        } else {
            resultLabel.text = "VLDofusBot initialization KO : ${errors.joinToString("") { "\n - $it" }}"
            resultLabel.foreground = Color.RED
        }
        retryButton.isVisible = !success
        return success
    }

    private fun prepareInit(initTask: InitTask) {
        initTask.label.foreground = Color.LIGHT_GRAY
        initTask.progressBar.isVisible = false
    }

    private fun startInit(initTask: InitTask, errors: ArrayList<String>) {
        initTask.label.foreground = Color.WHITE
        initTask.progressBar.isIndeterminate = true
        initTask.progressBar.isVisible = true
        initTask.progressBar.foreground = Color.LIGHT_GRAY
        try {
            initTask.function()
            initTask.success = true
        } catch (e: Throwable) {
            e.printStackTrace()
            errors.add(e.message ?: e.toString())
            initTask.success = false
        }
        val color = if (initTask.success) Color.GREEN else Color.RED
        initTask.progressBar.foreground = color
        initTask.label.foreground = color
        initTask.progressBar.isIndeterminate = false
        initTask.progressBar.maximum = 1
        initTask.progressBar.value = 1
    }

    private fun initFileManagers() {
        BreedAssetManager.initManager()
        CharacterManager.initManager()
        ConfigManager.initManager()
        HintManager.initManager()
    }

    private fun initEventStoreHandlers() {
        Reflections(VLDofusBot::class.java.packageName)
            .getSubTypesOf(EventHandler::class.java)
            .filter { !it.kotlin.isAbstract }
            .mapNotNull { it.kotlin.objectInstance ?: it.getConstructor().newInstance() }
            .forEach { EventStore.addEventHandler(it) }
    }

}