package fr.lewon.dofus.bot.gui.vldb.panes.config

import fr.lewon.dofus.bot.gui.vldb.panes.character.CharacterSelectionPanel
import fr.lewon.dofus.bot.sniffer.DofusMessageReceiverUtil
import fr.lewon.dofus.bot.util.filemanagers.impl.ConfigManager
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import java.awt.event.ItemEvent
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JLabel

object ConfigPanel : AbstractConfigPanel() {

    private val locateCursorLabel = JLabel("Locate cursor")
    private val locateCursorButton = JButton("Locate")
    private val displayOverlaysLabel = JLabel("Display overlays")
    private val displayOverlaysCheckBox = JCheckBox()
    private val archMonsterSoundLabel = JLabel("Arch monster sound")
    private val archMonsterSoundCheckBox = JCheckBox()
    private val questMonsterSoundLabel = JLabel("Quest monster sound")
    private val questMonsterSoundCheckBox = JCheckBox()
    private val networkInterfaceSelectionLabel = JLabel("Network interface")
    private val networkInterfaceSelectionComboBox =
        JComboBox(DofusMessageReceiverUtil.getNetworkInterfaceNames().toTypedArray())

    init {
        // Display overlays
        displayOverlaysCheckBox.isSelected = ConfigManager.readConfig().displayOverlays
        displayOverlaysLabel.toolTipText = "Display overlays using associated shortcuts"
        addLine(displayOverlaysLabel, displayOverlaysCheckBox)
        displayOverlaysCheckBox.addItemListener {
            updateDisplayOverlays(displayOverlaysCheckBox.isSelected)
        }

        // Arch monster sound
        archMonsterSoundCheckBox.isSelected = ConfigManager.readConfig().playArchMonsterSound
        archMonsterSoundLabel.toolTipText = "Plays a sound when you find an archmonster on a map"
        addLine(archMonsterSoundLabel, archMonsterSoundCheckBox)
        archMonsterSoundCheckBox.addItemListener {
            updateArchMonsterSound(archMonsterSoundCheckBox.isSelected)
        }

        // Quest monster sound
        questMonsterSoundCheckBox.isSelected = ConfigManager.readConfig().playQuestMonsterSound
        questMonsterSoundLabel.toolTipText = "Plays a sound when you find a quest monster on a map"
        addLine(questMonsterSoundLabel, questMonsterSoundCheckBox)
        questMonsterSoundCheckBox.addItemListener {
            updateQuestMonsterSound(questMonsterSoundCheckBox.isSelected)
        }

        // Network Interface GUI
        val savedNetworkInterfaceName = ConfigManager.readConfig().networkInterfaceName
            ?: networkInterfaceSelectionComboBox.getItemAt(0).also { updateNetworkInterface(it) }
        networkInterfaceSelectionComboBox.selectedItem = savedNetworkInterfaceName
        networkInterfaceSelectionLabel.toolTipText = "Network interface used to listen to game messages"
        addLine(networkInterfaceSelectionLabel, networkInterfaceSelectionComboBox)
        networkInterfaceSelectionComboBox.addItemListener { evt ->
            if (evt.stateChange == ItemEvent.SELECTED) {
                val networkInterfaceName = evt.item as String
                updateNetworkInterface(networkInterfaceName)
            }
        }

        // Cursor locator
        locateCursorButton.addActionListener { locatePoint() }
        addLine(locateCursorLabel, locateCursorButton)
    }

    private fun locatePoint() {
        val locatorFrame = CursorLocatorFrame("Click on position to register") {
            println("-----")
            val currentCharacter = CharacterSelectionPanel.cardList.selectedItem
            currentCharacter?.let { c ->
                GameSnifferUtil.getFirstConnection(c)?.let { connection ->
                    val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
                    JNAUtil.updateGameBounds(gameInfo)
                    val windowPos = JNAUtil.getGamePosition(connection.pid)
                    val pointAbsolute = PointAbsolute(it.x - windowPos.x, it.y - windowPos.y)
                    println("PointAbsolute(${pointAbsolute.x}, ${pointAbsolute.y})")
                    val pointRelative = ConverterUtil.toPointRelative(gameInfo, pointAbsolute)
                    println("PointRelative(${pointRelative.x}f, ${pointRelative.y}f)")
                    val uiPoint = ConverterUtil.toUIPoint(gameInfo, pointAbsolute)
                    println("UIPoint(${uiPoint.x}, ${uiPoint.y})")
                }
            } ?: println("Couldn't find dofus connection information.")
        }
        locatorFrame.isVisible = true
    }

    private fun updateDisplayOverlays(displayOverlays: Boolean) {
        ConfigManager.editConfig { it.displayOverlays = displayOverlays }
    }

    private fun updateArchMonsterSound(playArchMonsterSound: Boolean) {
        ConfigManager.editConfig { it.playArchMonsterSound = playArchMonsterSound }
    }

    private fun updateQuestMonsterSound(playQuestMonsterSound: Boolean) {
        ConfigManager.editConfig { it.playQuestMonsterSound = playQuestMonsterSound }
    }

    private fun updateNetworkInterface(networkInterfaceName: String) {
        ConfigManager.editConfig { it.networkInterfaceName = networkInterfaceName }
        GameSnifferUtil.updateNetworkInterface()
    }

}