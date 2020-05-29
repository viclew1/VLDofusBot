package fr.lewon.dofus.bot.ui

import fr.lewon.dofus.bot.json.DTBPoint
import fr.lewon.dofus.bot.json.PositionsByDirection
import fr.lewon.dofus.bot.util.*
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.awt.MouseInfo
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class DofusTreasureBotGUIController : Initializable {

    @FXML
    private lateinit var gameScreenRegionSelector: ChoiceBox<String>
    @FXML
    private lateinit var huntLevelSelector: ChoiceBox<String>
    @FXML
    private lateinit var autopilotCheckbox: CheckBox
    @FXML
    private lateinit var moveTimeoutInput: TextField
    @FXML
    private lateinit var logsList: ListView<LogItem>
    @FXML
    private lateinit var status: Label

    @FXML
    private lateinit var averageExecTimeLbl: Label
    @FXML
    private lateinit var successRateLbl: Label

    @FXML
    private lateinit var tabPane: TabPane
    @FXML
    private lateinit var scriptsVbox: VBox

    private lateinit var huntLevelTemplatePathByLevel: List<Pair<String, Int>>
    private lateinit var graphicsDevicesAndIds: List<Pair<GraphicsDevice, String>>

    val hintsIdsByName: MutableMap<String, List<String>> =
        DTBRequestProcessor.getAllHints()
    private val huntsResults = ArrayList<Pair<Long, Boolean>>()


    @FXML
    override fun initialize(location: URL, resources: ResourceBundle?) {
        this.graphicsDevicesAndIds = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices
            .map { Pair(it, it.iDstring) }

        graphicsDevicesAndIds.forEach {
            this.gameScreenRegionSelector.items.add(it.second)
        }
        this.gameScreenRegionSelector.value = DTBConfigManager.config.gameScreenRegion
        gameScreenRegionSelector.selectionModel.selectedIndexProperty()
            .addListener { _, _, newVal ->
                DTBConfigManager.editConfig {
                    it.gameScreenRegion = graphicsDevicesAndIds[newVal.toInt()].second
                }
            }


        moveTimeoutInput.textProperty().addListener { _, _, newValue ->
            if (!newValue.matches("\\d*".toRegex())) {
                moveTimeoutInput.text = newValue.replace("[^\\d]".toRegex(), "")
            }
            if (moveTimeoutInput.text.isEmpty()) {
                moveTimeoutInput.text = "0"
            }
            DTBConfigManager.editConfig { it.moveTimeout = moveTimeoutInput.text.toInt() }
        }

        val huntLvlRegex = "hunt_([0-9]+)\\.png".toRegex()
        huntLevelTemplatePathByLevel = File("templates/hunt_level_images").listFiles()
            ?.mapNotNull { huntLvlRegex.find(it.name) }
            ?.map { it.destructured.component1().toInt() }
            ?.map { Pair("templates/hunt_level_images/hunt_$it.png", it) }
            ?.sortedByDescending { it.second }
            ?: emptyList()
        huntLevelTemplatePathByLevel.forEach { huntLevelSelector.items.add(it.second.toString()) }
        huntLevelSelector.value = DTBConfigManager.config.huntLevel.toString()

        huntLevelSelector.selectionModel.selectedIndexProperty()
            .addListener { _, _, newVal ->
                DTBConfigManager.editConfig {
                    it.huntLevel = huntLevelSelector.items[newVal.toInt()].toInt()
                }
            }

        hintsIdsByName["Phorreur xxxx"] = listOf("PHO")
        autopilotCheckbox.isSelected = DTBConfigManager.config.autopilot
    }

    @FXML
    fun onAutopilotCheckboxAction(actionEvent: ActionEvent) {
        DTBConfigManager.editConfig { it.autopilot = autopilotCheckbox.isSelected }
    }

    @FXML
    private fun refreshScripts(event: Event) {
        scriptsVbox.children.clear()
        val scripts = File("scripts").listFiles() ?: emptyArray()
        for (script in scripts) {
            val lbl = Label(script.name)
            val btn = Button("X")
            btn.setOnAction {
                processBtnExecution(
                    execution = { runScript(script, it) },
                    startMessage = "Executing script ${script.name} ...",
                    successMessage = "OK",
                    failMessage = "KO"
                )
            }
            val pane = AnchorPane(
                lbl, btn
            )
            AnchorPane.setLeftAnchor(lbl, 0.0)
            AnchorPane.setRightAnchor(btn, 0.0)
            val sep = Separator()
            sep.padding = Insets(5.0, 0.0, 5.0, 0.0)

            scriptsVbox.children.add(pane)
            scriptsVbox.children.add(sep)
        }
    }

    private fun runScript(script: File, logItem: LogItem) {
        try {
            DofusBotScriptRunner.runScript(this@DofusTreasureBotGUIController, logItem, script)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private fun enableButtons(enabled: Boolean) {
        tabPane.tabs.forEach {
            it.content.disableProperty().value = !enabled
        }
    }

    fun registerPos(parentLogItem: LogItem?): Point {
        val screenBounds = getGameScreen().defaultConfiguration.bounds
        val logItem = log("Position will be registered in", parentLogItem)
        for (i in 3 downTo 1) {
            appendLog(logItem, " $i...")
            Thread.sleep(1000)
        }
        return MouseInfo.getPointerInfo().location
            .also {
                it.x -= screenBounds.x
                it.y -= screenBounds.y
            }
            .also { log("Done ! Cursor position : [${it.x}, ${it.y}]", parentLogItem) }
    }

    private fun registerAccess(direction: Directions) {
        processBtnExecution(
            execution = {
                val location = GameInfoUtil.getLocation(captureGameImage())
                    ?: error("Failed to retrieve current location.")
                val point = registerPos(it)
                DTBConfigManager.editConfig { config ->
                    val key = "${location.first}_${location.second}"
                    config.registeredMoveLocationsByMap.putIfAbsent(key, PositionsByDirection())
                    config.registeredMoveLocationsByMap[key]?.put(direction, DTBPoint(point.x, point.y))
                }
            },
            startMessage = "Select [$direction] move position on current map ...",
            successMessage = "OK",
            failMessage = "KO"
        )
    }

    @FXML
    fun selectLeftAccessZone(actionEvent: ActionEvent?) {
        registerAccess(Directions.LEFT)
    }

    @FXML
    fun selectRightAccessZone(actionEvent: ActionEvent?) {
        registerAccess(Directions.RIGHT)
    }

    @FXML
    fun selectBottomAccessZone(actionEvent: ActionEvent?) {
        registerAccess(Directions.BOTTOM)
    }

    @FXML
    fun selectTopAccessZone(actionEvent: ActionEvent?) {
        registerAccess(Directions.TOP)
    }

    @FXML
    fun selectMouseRestZone(actionEvent: ActionEvent?) {
        processBtnExecution(
            execution = {
                val point = registerPos(it)
                DTBConfigManager.editConfig { config ->
                    config.mouseRestPos = DTBPoint(point.x, point.y)
                }
            },
            startMessage = "Select rest mouse position ... ",
            successMessage = "OK",
            failMessage = "KO"
        )
    }

    @Synchronized
    fun processBtnExecution(
        execution: (LogItem) -> Unit,
        startMessage: String,
        successMessage: String,
        failMessage: String
    ) {
        Thread {
            enableButtons(false)
            Platform.runLater { status.style = "-fx-background-color: grey;"; }
            val logItem = log(startMessage)
            try {
                execution.invoke(logItem)
                closeLog(successMessage, logItem)
                Platform.runLater { status.style = "-fx-background-color: green;"; }
            } catch (e: Exception) {
                closeLog(failMessage, logItem)
                Platform.runLater { status.style = "-fx-background-color: red;"; }
            } finally {
                MatFlusher.releaseAll()
                log("----------")
                enableButtons(true)
            }
        }.start()
    }

    @Synchronized
    fun clearLogs() {
        Platform.runLater {
            logsList.items.clear()
        }
    }

    @Synchronized
    fun closeLog(message: String, parent: LogItem) {
        parent.closeLog(message)
        Platform.runLater {
            logsList.refresh()
            logsList.scrollTo(logsList.items.size)
        }
    }

    @Synchronized
    fun appendLog(logItem: LogItem, message: String) {
        logItem.message += message
        Platform.runLater {
            logsList.refresh()
        }
    }

    @Synchronized
    fun log(message: String, parent: LogItem? = null): LogItem {
        val newItem = LogItem(message)
        if (parent != null) {
            parent.addSubItem(newItem)
            Platform.runLater {
                logsList.refresh()
                logsList.scrollTo(logsList.items.size)
            }
        }
        Platform.runLater {
            while (logsList.items.size >= 10) {
                logsList.items.removeAt(0)
            }
            if (parent == null) logsList.items.add(newItem)
            logsList.refresh()
            logsList.scrollTo(logsList.items.size)
        }
        return newItem
    }

    @Synchronized
    fun updateStats(huntResult: Long, success: Boolean) {
        huntsResults.add(Pair(huntResult, success))
        Platform.runLater {
            val execMillisAverage = huntsResults
                .map { it.first }
                .average()
                .toLong()
            val minutes = execMillisAverage / (60 * 1000)
            val seconds = (execMillisAverage % (1000 * 60) / 1000).toString().padStart(2, '0')
            averageExecTimeLbl.text = "${minutes}min, ${seconds}s"

            val successCount = huntsResults
                .filter { it.second }
                .size
            successRateLbl.text = "$successCount / ${huntsResults.size}"
        }
    }

    @Synchronized
    fun captureGameImage(): BufferedImage {
        val screen = getGameScreen()
        val bounds = screen.defaultConfiguration.bounds
        return RobotUtil.screenShot(bounds.x, bounds.y, bounds.width, bounds.height)
    }

    @Synchronized
    fun getGameScreen(): GraphicsDevice {
        return this.graphicsDevicesAndIds
            .first { it.second == DTBConfigManager.config.gameScreenRegion }
            .first
    }

    @Synchronized
    fun getHuntLvlTemplatePath(): String {
        return this.huntLevelTemplatePathByLevel
            .first { it.second == DTBConfigManager.config.huntLevel }
            .first
    }

}