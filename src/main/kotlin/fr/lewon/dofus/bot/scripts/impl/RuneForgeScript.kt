package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameterType
import fr.lewon.dofus.bot.scripts.runeforge.DofusCharacteristic
import fr.lewon.dofus.bot.scripts.runeforge.RuneForgeLine
import fr.lewon.dofus.bot.scripts.runeforge.RuneForgeStrategy
import fr.lewon.dofus.bot.scripts.runeforge.strategies.DrakeHeadStrategy
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.util.DofusImages
import fr.lewon.dofus.bot.util.GameInfoUtil
import fr.lewon.dofus.bot.util.ImageUtil
import fr.lewon.dofus.bot.util.OCRUtil
import java.awt.image.BufferedImage

object RuneForgeScript : DofusBotScript("Runeforge") {

    private val runesXPos = listOf(1115, 1165, 1215)
    private val baseYPos = 343
    private val lastYPos = 811

    private val strategyTreatments = mapOf(
        Pair("Casque dragoeuf", DrakeHeadStrategy())
    )

    private val runeforgeStrategyParameter =
        DofusBotScriptParameter(
            "strategy",
            "Runeforge strategy",
            "",
            DofusBotScriptParameterType.CHOICE,
            strategyTreatments.keys.toList()
        )

    private val runesUsed = HashMap<String, Int>()

    override fun getParameters(): List<DofusBotScriptParameter> {
        return listOf(runeforgeStrategyParameter)
    }

    override fun getStats(): List<Pair<String, String>> {
        return runesUsed.map { it.key to "${it.value}" }
    }

    override fun getDescription(): String {
        return "Runeforge the selected item until perfection"
    }

    override fun doExecute(
        controller: DofusTreasureBotGUIController,
        logItem: LogItem?,
        parameters: Map<String, DofusBotScriptParameter>
    ) {
        val runeForgeStrategy =
            strategyTreatments[runeforgeStrategyParameter.value] ?: error("No runeforge strategy selected")
        val rfLogItem = controller.log("Runeforge strategy : ${runeforgeStrategyParameter.value}", logItem)

        checkFrame()

        val runeForgeLines = runeForgeStrategy.getRuneForgeLines().map { it.charac to it }.toMap()
        updateRuneForgeLines(runeForgeStrategy, runeForgeLines, controller, rfLogItem)
        while (!runeForgeStrategy.checkEnd(runeForgeLines)) {
            val toUpgrade = runeForgeStrategy.getRuneToPass(runeForgeLines)
            controller.log("Upgrading stat [${toUpgrade.second.caracName}]", rfLogItem)
            when {
                toUpgrade.second == DofusCharacteristic.AP -> {
                    applyApRune()
                }
                runeForgeLines.keys.contains(toUpgrade.second) -> {
                    upgradeItem(runeForgeLines.keys.indexOf(toUpgrade.second), toUpgrade.first)
                }
                else -> {
                    error("Impossible to upgrade this characteristic : ${toUpgrade.second.caracName}")
                }
            }
            sleep(600)
            updateRuneForgeLines(runeForgeStrategy, runeForgeLines, controller, rfLogItem)
        }
    }

    private fun updateRuneForgeLines(
        runeForgeStrategy: RuneForgeStrategy,
        runeForgeLines: Map<DofusCharacteristic, RuneForgeLine>,
        controller: DofusTreasureBotGUIController,
        logItem: LogItem?
    ) {
        val refreshLog = controller.log("Refreshing item stats ...", logItem)
        val currentImage = getCurrentValuesImage()
        val defaultLines = runeForgeStrategy.getRuneForgeLines().map { it.charac }
        val lines = OCRUtil.getAllLines(currentImage)
        for (i in lines.indices) {
            val charac = defaultLines[i]
            val rfLine = runeForgeLines[charac] ?: continue
            rfLine.current = parseInt(lines[i])
        }
        controller.closeLog("OK", refreshLog)
    }

    private fun parseInt(string: String): Int {
        val strFiltered = string.filter { it.isDigit() }
        if (strFiltered.isEmpty()) {
            return 0
        }
        return strFiltered.toInt()
    }

    private fun applyApRune() {
        val apRunePos = imgCenter("ap_rune.png", 0.8) ?: error("No AP rune found")
        doubleClickPoint(apRunePos)
        sleep(500)
        click("mix_button.png")
        sleep(2000)
    }

    private fun upgradeItem(characteristicIndex: Int, runePower: Int) {
        val xPos = runesXPos[runePower]
        val yDelta = (lastYPos - baseYPos) / 12
        val yPos = baseYPos + yDelta * characteristicIndex
        clickPoint(xPos, yPos)
    }

    private fun checkFrame() {
        GameInfoUtil.getFrameContent(
            captureGameImage(),
            DofusImages.RUNEFORGE_FRAME_TOP_TEMPLATE.path,
            DofusImages.RUNEFORGE_FRAME_BOT_TEMPLATE.path,
            DofusImages.RUNEFORGE_FRAME_LEFT_TEMPLATE.path,
            DofusImages.RUNEFORGE_FRAME_RIGHT_TEMPLATE.path
        ) ?: error("Couldn't find runeforge frame")
    }

    private fun getCurrentValuesImage(): BufferedImage {
        var subImage = captureGameImage().getSubimage(795, 324, 50, 508)
        subImage = ImageUtil.resizeImage(subImage, 3)
        return OCRUtil.keepWhiteOnImage(ImageUtil.bufferedImageToMat(subImage), false, 95.0)
    }

}