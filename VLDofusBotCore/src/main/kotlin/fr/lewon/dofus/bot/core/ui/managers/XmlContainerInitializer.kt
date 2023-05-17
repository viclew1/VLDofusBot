package fr.lewon.dofus.bot.core.ui.managers

import fr.lewon.dofus.bot.core.ui.UIBounds
import fr.lewon.dofus.bot.core.ui.xml.anchors.Anchor
import fr.lewon.dofus.bot.core.ui.xml.anchors.AnchorPoint
import fr.lewon.dofus.bot.core.ui.xml.containers.Container
import fr.lewon.dofus.bot.core.ui.xml.sizes.Dimension


object XmlContainerInitializer {

    fun initAll(container: Container) {
        container.parentContainer = UIBounds.buildRootContainer()
        initContainers(container, container)
        container.initRoot(container)
    }

    private fun initContainers(rootContainer: Container, container: Container) {
        flattenChildContainerIfNeeded(container)
        updateAnchorsParameters(container.anchors)
        addMissingAnchors(container)
        container.root = rootContainer
        for (subContainer in container.children) {
            subContainer.parentContainer = container
            initContainers(rootContainer, subContainer)
        }
    }

    private fun updateAnchorsParameters(anchors: List<Anchor>) {
        var firstWithoutPoint = true
        for (anchor in anchors) {
            if (anchor.relativePoint.isEmpty()) {
                anchor.relativePoint = AnchorPoint.TOPLEFT.name
            }
            val anchorPoint = when {
                anchor.point.isNotEmpty() -> AnchorPoint.valueOf(anchor.point)
                firstWithoutPoint -> {
                    firstWithoutPoint = false
                    AnchorPoint.TOPLEFT
                }
                anchor.relativePoint.isNotEmpty() -> AnchorPoint.valueOf(anchor.relativePoint)
                else -> error("Impossible")
            }
            anchor.point = anchorPoint.name
        }
    }

    private fun addMissingAnchors(container: Container) {
        if (container.anchors.isEmpty()) {
            addAnchorIfNoneMatchesCondition(container, AnchorPoint.TOPLEFT)
        }
    }

    private fun addAnchorIfNoneMatchesCondition(
        container: Container,
        anchorPointToAdd: AnchorPoint,
        condition: (AnchorPoint) -> Boolean = { false }
    ) {
        if (container.anchors.none { condition(AnchorPoint.valueOf(it.point)) }) {
            val anchor = Anchor(anchorPointToAdd.name, anchorPointToAdd.name, absDimension = Dimension("0", "0"))
            container.anchors.add(anchor)
        }
    }

    private fun flattenChildContainerIfNeeded(container: Container) {
        if (container.children.size != 1) {
            return
        }
        val child = container.children.first()
        if ((container.anchors.isEmpty() || child.anchors.isEmpty())
            && (container.sizes.isEmpty() || child.sizes.isEmpty())
            && (container.name.isEmpty() || child.name.isEmpty())
        ) {
            if (container.name.isEmpty()) {
                container.name = child.name
            }
            container.children.clear()
            container.children.addAll(child.children)
            if (child.anchors.isNotEmpty()) {
                container.anchors = child.anchors
            }
            if (container.sizes.isEmpty()) {
                container.sizes = child.sizes
            }
            container.children.forEach { it.parentContainer = container }
            flattenChildContainerIfNeeded(container)
        }
    }

}