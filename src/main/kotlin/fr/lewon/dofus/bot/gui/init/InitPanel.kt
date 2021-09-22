package fr.lewon.dofus.bot.gui.init

import fr.lewon.dofus.bot.VLDofusBot
import fr.lewon.dofus.bot.sniffer.DofusMessageReceiver
import fr.lewon.dofus.bot.sniffer.DofusMessageReceiverUtil
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.WindowsUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.listeners.KeyboardListener
import fr.lewon.dofus.bot.util.logs.VldbLogger
import fr.lewon.dofus.bot.util.manager.VldbManager
import net.miginfocom.swing.MigLayout
import nu.pattern.OpenCV
import org.reflections.Reflections
import java.awt.Color
import javax.swing.*


object InitPanel : JPanel(MigLayout("ins 10")) {

    private val decompiledLabel = JLabel("Dofus decompiled ...").also { it.foreground = Color.LIGHT_GRAY }
    private val decompiledProgressBar = JProgressBar().also { it.isVisible = false }

    private val snifferHandlersLabel = JLabel("Sniffer handlers ...").also { it.foreground = Color.LIGHT_GRAY }
    private val snifferHandlersProgressBar = JProgressBar().also { it.isVisible = false }

    private val dofusManagersLabel = JLabel("Dofus Managers ...").also { it.foreground = Color.LIGHT_GRAY }
    private val dofusManagersProgressBar = JProgressBar().also { it.isVisible = false }

    private val openCvLabel = JLabel("OpenCV ...").also { it.foreground = Color.LIGHT_GRAY }
    private val openCvProgressBar = JProgressBar().also { it.isVisible = false }

    private val keyboardListenerLabel = JLabel("Keyboard Listener ...").also { it.foreground = Color.LIGHT_GRAY }
    private val keyboardListenerProgressBar = JProgressBar().also { it.isVisible = false }

    private val initOkLabel = JLabel("VLDofusBot initialization OK !").also {
        it.foreground = Color.GREEN
        it.isVisible = false
    }

    init {
        background = Color.DARK_GRAY
        addLine(decompiledLabel, decompiledProgressBar)
        addLine(snifferHandlersLabel, snifferHandlersProgressBar)
        addLine(dofusManagersLabel, dofusManagersProgressBar)
        addLine(openCvLabel, openCvProgressBar)
        addLine(keyboardListenerLabel, keyboardListenerProgressBar, false)

        add(JPanel().also { it.background = background }, "span 2, grow, pushy, wrap")
        val okLabelPanel = JPanel(MigLayout("", "[center, grow]"))
        okLabelPanel.background = background
        okLabelPanel.add(initOkLabel)
        add(okLabelPanel, "dock south")
    }

    private fun addLine(leftComponent: JComponent, rightComponent: JComponent, separator: Boolean = true) {
        add(leftComponent)
        add(rightComponent, "width 60, height 8, al right, wrap")
        if (separator) add(JSeparator(JSeparator.HORIZONTAL), "span 2 1, width max, wrap")
    }

    fun initAll() {
        var success = true
        success = success && startInit(decompiledLabel, decompiledProgressBar)
        { DofusMessageReceiverUtil.prepareNetworkManagers() }
        success = success && startInit(snifferHandlersLabel, snifferHandlersProgressBar) { initEventStoreHandlers() }
        success = success && startInit(dofusManagersLabel, dofusManagersProgressBar) { initDofusManagers() }
        success = success && startInit(openCvLabel, openCvProgressBar) { OpenCV.loadLocally() }
        success = success && startInit(keyboardListenerLabel, keyboardListenerProgressBar) { KeyboardListener.start() }

        if (success) {
            initOkLabel.isVisible = true
            DofusMessageReceiver.start()
            WindowsUtil.updateGameBounds()
            WaitUtil.sleep(2000)
        }
    }

    private fun startInit(label: JLabel, progressBar: JProgressBar, function: () -> Unit): Boolean {
        label.foreground = Color.WHITE
        progressBar.isIndeterminate = true
        progressBar.isVisible = true
        var success = true
        try {
            function.invoke()
        } catch (e: Throwable) {
            VldbLogger.error(e.message ?: e.toString())
            success = false
        }
        val color = if (success) Color.GREEN else Color.RED
        progressBar.foreground = color
        label.foreground = color
        progressBar.isIndeterminate = false
        progressBar.maximum = 1
        progressBar.value = 1
        return success
    }

    private fun initDofusManagers() {
        Reflections(VldbManager::class.java.packageName)
            .getSubTypesOf(VldbManager::class.java)
            .mapNotNull { it.kotlin.objectInstance }
            .forEach { it.forceInit() }
        Reflections(VLDofusBot::class.java.packageName)
            .getSubTypesOf(VldbManager::class.java)
            .mapNotNull { it.kotlin.objectInstance }
            .forEach { it.forceInit() }
    }

    private fun initEventStoreHandlers() {
        Reflections(VLDofusBot::class.java.packageName)
            .getSubTypesOf(EventHandler::class.java)
            .filter { !it.kotlin.isAbstract }
            .mapNotNull { it.kotlin.objectInstance ?: it.getConstructor().newInstance() }
            .forEach {
                EventStore.addEventHandler(it)
            }
    }

}