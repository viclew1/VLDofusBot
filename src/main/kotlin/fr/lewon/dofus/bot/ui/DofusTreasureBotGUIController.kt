package fr.lewon.dofus.bot.ui

import fr.lewon.dofus.bot.json.DTBPoint
import fr.lewon.dofus.bot.json.PositionsByDirection
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameterType
import fr.lewon.dofus.bot.scripts.impl.*
import fr.lewon.dofus.bot.util.*
import javafx.application.Platform
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import org.controlsfx.control.ToggleSwitch
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import java.net.URI
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class DofusTreasureBotGUIController : Initializable {

    @FXML
    private lateinit var gameScreenRegionSelector: ChoiceBox<String>
    @FXML
    private lateinit var huntLevelSelector: ChoiceBox<String>
    @FXML
    private lateinit var moveTimeoutInput: TextField
    @FXML
    private lateinit var logsTextArea: TextArea
    @FXML
    private lateinit var status: Label
    @FXML
    private lateinit var scriptSelector: ChoiceBox<String>
    @FXML
    private lateinit var scriptDescriptionTextArea: TextArea
    @FXML
    private lateinit var parametersVBox: VBox
    @FXML
    private lateinit var scriptParameterDescriptionTextArea: TextArea
    @FXML
    private lateinit var scriptNameLbl: Label
    @FXML
    private lateinit var execTimeLbl: Label
    @FXML
    private lateinit var tabPane: TabPane
    @FXML
    private lateinit var logTab: Tab
    @FXML
    private lateinit var configTabContent: VBox
    @FXML
    private lateinit var scriptTabContent: VBox
    @FXML
    private lateinit var logTabContent: VBox
    @FXML
    private lateinit var stopScriptBtn: Button
    @FXML
    private lateinit var startScriptBtn: Button
    @FXML
    private lateinit var statsTableView: TableView<Pair<String, String>>
    @FXML
    private lateinit var statTableColumn: TableColumn<Pair<String, String>, String>
    @FXML
    private lateinit var valueTableColumn: TableColumn<Pair<String, String>, String>

    private var scriptRunningProperty: BooleanProperty = SimpleBooleanProperty(false)
    private lateinit var huntLevelTemplatePathByLevel: List<Pair<String, Int>>
    private lateinit var graphicsDevicesAndIds: List<Pair<GraphicsDevice, String>>

    var runningBtnThread: Thread? = null
    var shouldKillBtnThread = false

    private val logs = LinkedList<LogItem>()
    val hintsIdsByName: MutableMap<String, List<String>> =
        DTBRequestProcessor.getAllHints()
    private val scriptsByName = listOf(
        ChainHuntsScript,
        ResumeHuntScript,
        FetchAHuntScript,
        ReachHuntStartScript,
        FightScript
    ).map { it.name to it }
        .toMap()

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

        moveTimeoutInput.text = DTBConfigManager.config.moveTimeout.toString()
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
        scriptsByName.keys.forEach { scriptSelector.items.add(it) }
        scriptSelector.selectionModel.selectedIndexProperty()
            .addListener { _, _, newVal ->
                val newScriptName = scriptSelector.items[newVal.toInt()]
                val newScript = scriptsByName[newScriptName] ?: error("Script [$newScriptName] not found")
                scriptParameterDescriptionTextArea.text = ""
                editScriptParametersVbox(newScript)
                editDescription(newScript)
            }

        for (script in scriptsByName.values) {
            val scriptParameters = script.getParameters()
            DTBConfigManager.config.scriptParameters.putIfAbsent(script.name, ArrayList(scriptParameters))
            val registeredParameters = DTBConfigManager.config.scriptParameters[script.name] ?: ArrayList()
            for (param in scriptParameters) {
                val registeredParam = registeredParameters.firstOrNull { param.key == it.key }
                if (registeredParam == null) {
                    registeredParameters.add(param)
                } else {
                    param.value = registeredParam.value
                }
            }
        }

        parametersVBox.disableProperty().bind(scriptRunningProperty)
        configTabContent.disableProperty().bind(scriptRunningProperty)
        startScriptBtn.disableProperty().bind(scriptRunningProperty)
        stopScriptBtn.disableProperty().bind(scriptRunningProperty.not())

        statsTableView.placeholder = Label("No stat yet for this script")
        statTableColumn.cellValueFactory = PropertyValueFactory("first")
        valueTableColumn.cellValueFactory = PropertyValueFactory("second")
    }

    private fun editDescription(newScript: DofusBotScript) {
        scriptDescriptionTextArea.text = newScript.getDescription()
    }

    private fun editScriptParametersVbox(newScript: DofusBotScript) {
        parametersVBox.children.clear()
        val scriptParameters = ArrayList(newScript.getParameters())
        scriptParameters.forEach {
            val nameLbl = Label(it.key)
            val inputField = buildInputField(newScript, it)
            inputField.prefWidth = 60.0
            inputField.focusedProperty().addListener { _, _, newVal ->
                if (newVal) {
                    scriptParameterDescriptionTextArea.text = it.description
                }
            }
            val borderPane = BorderPane()
            borderPane.left = nameLbl
            borderPane.right = inputField
            VBox.setMargin(borderPane, Insets(5.0, 10.0, 5.0, 10.0))
            parametersVBox.children.add(borderPane)
        }
    }

    private fun buildInputField(script: DofusBotScript, param: DofusBotScriptParameter): Control {
        val onChange: (String) -> (Unit) = {
            param.value = it
            DTBConfigManager.editConfig { conf ->
                conf.scriptParameters[script.name]
                    ?.firstOrNull { p -> p.key == param.key }
                    ?.let { p -> p.value = it }
            }
            editDescription(script)
        }
        return when (param.type) {
            DofusBotScriptParameterType.INTEGER -> TextField(param.value).also {
                it.textFormatter = TextFormatter<Int> { newVal ->
                    newVal.takeIf { newVal.text.matches(Regex("[0-9]*")) }
                }
                it.textProperty().addListener { _, _, newVal -> onChange(newVal) }
            }
            DofusBotScriptParameterType.BOOLEAN -> ToggleSwitch().also {
                it.isSelected = param.value.toBoolean()
                it.selectedProperty().addListener { _, _, newVal -> onChange(newVal.toString()) }
            }
            else -> TextField(param.value).also {
                it.textProperty().addListener { _, _, newVal -> onChange(newVal) }
            }
        }
    }

    @FXML
    private fun runScript(actionEvent: ActionEvent?) {
        val script = scriptsByName[scriptSelector.value] ?: error("Script [${scriptSelector.value}] not found")
        processBtnExecution(
            execution = {
                Platform.runLater {
                    scriptNameLbl.text = "[${script.name}]"
                    scriptDescriptionTextArea.text = script.getDescription()
                }
                script.execute(this, it)
            },
            startMessage = "Executing script ${scriptSelector.value} ...",
            successMessage = "OK",
            failMessageBuilder = { "KO - ${it.localizedMessage}" },
            guiUpdater = {
                statsTableView.items.clear()
                script.getStats().forEach {
                    statsTableView.items.add(it)
                }
            }
        )
    }

    @FXML
    private fun stopScript(actionEvent: ActionEvent?) {
        shouldKillBtnThread = true
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
            failMessageBuilder = { "KO - ${it.localizedMessage}" }
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

    @Synchronized
    fun processBtnExecution(
        execution: (LogItem) -> Unit,
        startMessage: String,
        successMessage: String,
        failMessageBuilder: (Exception) -> String,
        guiUpdater: () -> Unit = {}
    ) {
        shouldKillBtnThread = false
        scriptRunningProperty.value = true
        val updateExecDurationTimer = Timer()
        val start = System.currentTimeMillis()
        updateExecDurationTimer.schedule(object : TimerTask() {
            override fun run() {
                val elapsed = System.currentTimeMillis() - start
                val hours = elapsed / (3600 * 1000)
                val minutes = (elapsed - hours * 3600 * 1000) / (60 * 1000)
                val seconds =
                    ((elapsed - hours * 3600 * 1000 - minutes * 60 * 1000) / 1000).toString().padStart(2, '0')
                Platform.runLater {
                    execTimeLbl.text = "Time : ${hours}H ${minutes}M ${seconds}S"
                    Platform.runLater { guiUpdater.invoke() }
                }
            }
        }, 1000, 1000)

        clearLogs()
        Platform.runLater {
            statsTableView.items.clear()
            tabPane.selectionModel.select(logTab)
        }
        val logItem = log(startMessage)
        runningBtnThread = Thread {
            Platform.runLater { status.style = "-fx-background-color: grey;" }
            try {
                execution.invoke(logItem)
                closeLog(successMessage, logItem)
                Platform.runLater { status.style = "-fx-background-color: green;" }
            } catch (e: Exception) {
                closeLog(failMessageBuilder.invoke(e), logItem)
                Platform.runLater { status.style = "-fx-background-color: red;" }
            } finally {
                scriptRunningProperty.value = false
                updateExecDurationTimer.cancel()
                updateExecDurationTimer.purge()
                MovesHistory.clearHistory()
                MatFlusher.releaseAll()
            }
        }
        runningBtnThread?.start()

        Thread {
            Thread.sleep(500)
            while (runningBtnThread?.isAlive == true) {
                Thread.sleep(500)
                if (shouldKillBtnThread) {
                    runningBtnThread?.stop()
                    closeLog("Execution interrupted", logItem)
                    Platform.runLater { status.style = "-fx-background-color: red;" }
                    updateExecDurationTimer.cancel()
                    updateExecDurationTimer.purge()
                    MatFlusher.releaseAll()
                    shouldKillBtnThread = false
                    break
                }
            }
        }.start()
    }

    @Synchronized
    private fun updateLogs() {
        Platform.runLater {
            logsTextArea.text = logs.joinToString("\n") + "\n "
            logsTextArea.scrollTop = Double.MAX_VALUE
        }
    }

    @Synchronized
    fun clearLogs() {
        logs.clear()
        updateLogs()
    }

    @Synchronized
    fun closeLog(message: String, parent: LogItem) {
        parent.closeLog(message)
        updateLogs()
    }

    @Synchronized
    fun appendLog(logItem: LogItem, message: String) {
        logItem.message += message
        updateLogs()
    }

    @Synchronized
    fun log(message: String, parent: LogItem? = null): LogItem {
        val newItem = LogItem(message)
        parent?.addSubItem(newItem) ?: logs.add(newItem)
        while (logs.size >= 5) {
            logs.removeFirst()
        }
        updateLogs()
        return newItem
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

    fun openGithubPage(actionEvent: ActionEvent) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI("https://github.com/viclew1/DTB"))
        }
    }

    fun locateCursor(actionEvent: ActionEvent) {
        processBtnExecution(
            execution = {
                val point = registerPos(it)
                log("Location : [${point.x},${point.y}]", it)
            },
            startMessage = "Locate mouse coordinates on screen ...",
            successMessage = "OK",
            failMessageBuilder = { "KO - ${it.localizedMessage}" }
        )
    }

}