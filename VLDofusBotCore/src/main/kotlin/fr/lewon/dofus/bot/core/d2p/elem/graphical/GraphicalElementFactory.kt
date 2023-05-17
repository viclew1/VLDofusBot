package fr.lewon.dofus.bot.core.d2p.elem.graphical

import fr.lewon.dofus.bot.core.d2p.elem.graphical.impl.*

object GraphicalElementFactory {

    fun getGraphicalElementData(elementId: Int, elementType: Int): GraphicalElementData {
        return when (GraphicalElementTypes.fromInt(elementType)) {
            GraphicalElementTypes.NORMAL -> NormalGraphicalElementData(elementId, elementType)
            GraphicalElementTypes.BOUNDING_BOX -> BoundingBoxGraphicalElementData(elementId, elementType)
            GraphicalElementTypes.ANIMATED -> AnimatedGraphicalElementData(elementId, elementType)
            GraphicalElementTypes.ENTITY -> EntityGraphicalElementData(elementId, elementType)
            GraphicalElementTypes.PARTICLES -> ParticlesGraphicalElementData(elementId, elementType)
            GraphicalElementTypes.BLENDED -> BlendedGraphicalElementData(elementId, elementType)
            else -> error("Unknown graphical element data type $elementType for element $elementId")
        }
    }

}