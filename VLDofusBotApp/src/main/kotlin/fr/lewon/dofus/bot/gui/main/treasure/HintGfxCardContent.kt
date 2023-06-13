package fr.lewon.dofus.bot.gui.main.treasure

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.util.AppColors
import kotlinx.coroutines.delay

@Composable
fun HintGfxCardContent(gfxId: Int, imageCache: TreasureHintImageCache) {
    val gfxPainter = rememberSaveable {
        mutableStateOf(imageCache.getPainter(gfxId))
    }
    LaunchedEffect(gfxId) {
        if (!imageCache.isLoaded(gfxId)) {
            imageCache.loadImagePainter(gfxId)
        }
        gfxPainter.value = imageCache.getPainter(gfxId)
        while (gfxPainter.value == null) {
            delay(100)
            gfxPainter.value = imageCache.getPainter(gfxId)
        }
    }
    val painter = gfxPainter.value
    Column(Modifier.fillMaxSize().padding(3.dp)) {
        Row {
            SelectionContainer(Modifier.align(Alignment.CenterVertically)) {
                CommonText(
                    gfxId.toString(),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(Modifier.height(5.dp))
        Box(Modifier.fillMaxSize()) {
            if (painter != null) {
                Image(painter, "", modifier = Modifier.align(Alignment.Center))
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize().padding(15.dp),
                    color = AppColors.primaryLightColor
                )
            }
        }
    }
}