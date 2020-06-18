package fr.lewon.dofus.bot

import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage
import nu.pattern.OpenCV
import kotlin.system.exitProcess


class DofusTreasureBotFXApp : Application() {

    override fun start(primaryStage: Stage) {
        val loader = FXMLLoader(javaClass.getResource("/scenes/main_scene_v2.fxml"))
        val rootElement = loader.load() as VBox
        val scene = Scene(rootElement, rootElement.prefWidth, rootElement.prefHeight)

        primaryStage.minWidth = rootElement.minWidth
        primaryStage.minHeight = rootElement.minHeight
        primaryStage.title = "Dofus Treasure Bot"
        primaryStage.isAlwaysOnTop = true
        primaryStage.scene = scene

        // show the GUI
        primaryStage.show()
        // set the proper behavior on closing the application
        val controller: DofusTreasureBotGUIController = loader.getController()
        primaryStage.setOnCloseRequest { exitProcess(0) }
    }

    fun run() {
        launch()
    }

}

fun main() {
    OpenCV.loadLocally()
    System.setProperty("idea.use.native.fs.for.win", "false")
    DofusTreasureBotFXApp().run()
}
