package fr.lewon.dofus.bot.gui.vldb.panes.status

import fr.lewon.dofus.bot.gui.MainFrame
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.gui.util.UiResource
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.time.LocalDateTime
import java.util.concurrent.ArrayBlockingQueue
import javax.swing.*

object StatusPanel : JPanel(MigLayout("insets 0")) {


    private val statusLabel = JLabel()
    private val historyButton = JButton()

    private val historyPopupMenu = JPopupMenu().also { it.add(JMenuItem("No history yet")) }

    private val oldMessages = ArrayBlockingQueue<String>(10)

    init {
        background = AppColors.DEFAULT_UI_COLOR

        val imageData = UiResource.HISTORY.imageData
        val filledImageData = UiResource.HISTORY.filledImageData
        historyButton.icon =
            ImageIcon(ImageUtil.getScaledImageKeepHeight(imageData, MainFrame.FOOTER_HEIGHT))
        historyButton.rolloverIcon =
            ImageIcon(ImageUtil.getScaledImageKeepHeight(filledImageData, MainFrame.FOOTER_HEIGHT))
        historyButton.pressedIcon = historyButton.rolloverIcon
        historyButton.isContentAreaFilled = false

        add(historyButton, "h max")
        statusLabel.insets.set(0, 0, 0, 0)
        add(statusLabel, "h max")
        historyButton.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {
                showHistory()
            }
        })
    }

    private fun showHistory() {
        val y = -8 - historyPopupMenu.components.size * 22
        historyPopupMenu.show(historyButton, 0, y)
    }

    fun changeText(character: DofusCharacter, text: String) {
        if (statusLabel.text.isNotEmpty()) {
            if (oldMessages.size == 0) {
                historyPopupMenu.remove(0)
            }
            if (!oldMessages.offer(statusLabel.text)) {
                oldMessages.poll()
                historyPopupMenu.remove(0)
                oldMessages.offer(statusLabel.text)
            }
            historyPopupMenu.add(JMenuItem(statusLabel.text))
        }
        val ldt = LocalDateTime.now()
        val hours = StringUtils.leftPad(ldt.hour.toString(), 2, "0")
        val minutes = StringUtils.leftPad(ldt.minute.toString(), 2, "0")
        val seconds = StringUtils.leftPad(ldt.second.toString(), 2, "0")
        val timeStamp = "$hours:$minutes:$seconds"
        statusLabel.text = "$timeStamp : ${character.pseudo} - $text"
    }

}