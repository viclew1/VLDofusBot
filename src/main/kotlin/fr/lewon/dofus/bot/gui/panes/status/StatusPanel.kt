package fr.lewon.dofus.bot.gui.panes.status

import fr.lewon.dofus.bot.gui.VldbMainFrame
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
    private val historyLabel = JButton()

    private val historyPopupMenu = JPopupMenu()

    private val oldMessages = ArrayBlockingQueue<String>(10)

    init {
        background = AppColors.DEFAULT_UI_COLOR

        val imageData = UiResource.HISTORY.imageData
        val filledImageData = UiResource.HISTORY.filledImageData
        historyLabel.icon =
            ImageIcon(ImageUtil.getScaledImageKeepHeight(imageData, VldbMainFrame.FOOTER_HEIGHT))
        historyLabel.rolloverIcon =
            ImageIcon(ImageUtil.getScaledImageKeepHeight(filledImageData, VldbMainFrame.FOOTER_HEIGHT))
        historyLabel.pressedIcon = historyLabel.rolloverIcon
        historyLabel.isContentAreaFilled = false

        add(historyLabel, "h max")
        statusLabel.insets.set(0, 0, 0, 0)
        add(statusLabel, "h max")
        historyLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent?) {
                showHistory()
            }

            override fun mouseExited(e: MouseEvent?) {
                historyPopupMenu.isVisible = false
            }
        })
    }

    private fun showHistory() {
        val x = historyLabel.width
        val y = -historyPopupMenu.height + historyLabel.height / 2
        historyPopupMenu.show(historyLabel, x, y)
        historyPopupMenu.show(historyLabel, x, y)
    }

    fun changeText(character: DofusCharacter, text: String) {
        if (statusLabel.text.isNotEmpty()) {
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