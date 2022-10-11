package fr.lewon.dofus.bot

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import fr.lewon.dofus.bot.gui2.custom.handPointerIcon
import fr.lewon.dofus.bot.gui2.init.InitBottomBar
import fr.lewon.dofus.bot.gui2.init.InitContent
import fr.lewon.dofus.bot.gui2.init.InitUIUtil
import fr.lewon.dofus.bot.gui2.main.MainContent
import fr.lewon.dofus.bot.gui2.main.MainContentUIUtil
import fr.lewon.dofus.bot.gui2.util.AppInfo
import fr.lewon.dofus.bot.gui2.util.UiResource
import fr.lewon.dofus.bot.gui2.util.getScaledImage
import java.awt.Dimension
import kotlin.system.exitProcess

class VLDofusBot

private lateinit var composeWindow: ComposeWindow
lateinit var windowState: WindowState
private var currentPage = mutableStateOf(AppPage.INITIALIZER)
fun main() = application {
    windowState = rememberWindowState(size = currentPage.value.defaultSize)
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        undecorated = true,
        resizable = currentPage.value.resizable,
        icon = UiResource.TASKBAR_LOGO.imageData.getScaledImage(32).toPainter(),
    ) {
        composeWindow = remember { window }
        val appPage = currentPage.value
        AppInfo.mainTheme(appPage.typography) {
            Scaffold(
                topBar = { WindowDraggableArea { topBar() } },
                bottomBar = { appPage.bottomBarContent() },
                content = { appPage.pageContent() },
                drawerGesturesEnabled = true,
            )
        }
    }
    LaunchedEffect(true) {
        updatePage(AppPage.INITIALIZER)
        InitUIUtil.initAll()
    }
}

fun updatePage(appPage: AppPage) {
    composeWindow.title = appPage.pageTitle()
    composeWindow.isResizable = appPage.resizable
    composeWindow.minimumSize = Dimension(appPage.minSize.width.value.toInt(), appPage.minSize.height.value.toInt())
    composeWindow.size = Dimension(appPage.defaultSize.width.value.toInt(), appPage.defaultSize.height.value.toInt())
    currentPage.value = appPage
}

@Composable
private fun topBar() {
    TopAppBar(
        title = { Text(currentPage.value.pageTitle()) },
        navigationIcon = { Image(UiResource.GLOBAL_LOGO.imageData.getScaledImage(64).toPainter(), "") },
        actions = {
            appBarButton("—") { windowState.isMinimized = !windowState.isMinimized }
            appBarButton("X") { exitProcess(0) }
        }
    )
}

@Composable
private fun appBarButton(text: String, action: () -> Unit) {
    Button(
        action,
        Modifier.width(30.dp).fillMaxHeight(),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background),
        elevation = null
    ) {
        Row(Modifier.handPointerIcon().fillMaxSize()) {
            Text(text, Modifier.align(Alignment.CenterVertically).fillMaxWidth(), textAlign = TextAlign.Center)
        }
    }
}

enum class AppPage(
    val pageTitle: () -> String,
    val typography: Typography,
    val resizable: Boolean,
    val defaultSize: DpSize,
    val minSize: DpSize = DpSize(100.dp, 100.dp),
    val pageContent: @Composable () -> Unit,
    val bottomBarContent: @Composable () -> Unit
) {
    INITIALIZER(
        { "${AppInfo.APP_NAME} Initializer" },
        Typography(FontFamily.Monospace),
        false,
        DpSize(300.dp, 500.dp),
        pageContent = { InitContent() },
        bottomBarContent = { InitBottomBar() }
    ),
    MAIN(
        { "${AppInfo.APP_NAME} - ${MainContentUIUtil.mainContentUIState.value.currentAppContent.title}" },
        Typography(FontFamily.SansSerif),
        true,
        DpSize(1300.dp, 850.dp),
        DpSize(1200.dp, 750.dp),
        pageContent = { MainContent() },
        bottomBarContent = {}
    );
}