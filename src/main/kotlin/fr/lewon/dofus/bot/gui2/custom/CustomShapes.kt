package fr.lewon.dofus.bot.gui2.custom

import androidx.compose.foundation.shape.GenericShape

object CustomShapes {

    fun buildTrapezoidShape(
        topLeftDeltaRatio: Float = 0f,
        topRightDeltaRatio: Float = 0f,
        bottomLeftDeltaRatio: Float = 0f,
        bottomRightDeltaRatio: Float = 0f,
    ): GenericShape {
        return GenericShape { size, _ ->
            val topLeftDx = size.width * topLeftDeltaRatio
            val topRightDx = size.width * topRightDeltaRatio
            val bottomLeftDx = size.width * bottomLeftDeltaRatio
            val bottomRightDx = size.width * bottomRightDeltaRatio
            moveTo(topLeftDx, 0f)
            lineTo(size.width - topRightDx, 0f)
            lineTo(size.width - bottomRightDx, size.height)
            lineTo(bottomLeftDx, size.height)
        }
    }

}