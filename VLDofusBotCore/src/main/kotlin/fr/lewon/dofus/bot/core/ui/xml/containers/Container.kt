package fr.lewon.dofus.bot.core.ui.xml.containers

import fr.lewon.dofus.bot.core.ui.UIPoint
import fr.lewon.dofus.bot.core.ui.UIRectangle
import fr.lewon.dofus.bot.core.ui.managers.UIIconManager
import fr.lewon.dofus.bot.core.ui.xml.anchors.Anchor
import fr.lewon.dofus.bot.core.ui.xml.anchors.AnchorPoint
import fr.lewon.dofus.bot.core.ui.xml.sizes.Dimension
import fr.lewon.dofus.bot.core.ui.xml.sizes.Size
import org.w3c.dom.Element
import javax.xml.bind.annotation.*
import kotlin.math.absoluteValue

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CONTAINER")
data class Container(
    @field:XmlAttribute
    var name: String = "",

    @field:XmlElement(name = "Size")
    var sizes: ArrayList<Size> = ArrayList(),

    @field:XmlElementWrapper(name = "Anchors")
    @field:XmlElement(name = "Anchor")
    var anchors: ArrayList<Anchor> = ArrayList(),

    @field:XmlElement(name = "uri")
    var uri: String = "",

    @field: XmlElement(name = "themeDataId")
    var themeDataId: String = "",

    @field:XmlAnyElement
    var childrenPremises: ArrayList<Element> = ArrayList()
) {

    val children = ArrayList<Container>()

    var defaultTopLeftPosition: UIPoint? = null
    var defaultSize: UIPoint? = null
    lateinit var root: Container
    lateinit var parentContainer: Container

    @delegate:Transient
    val bounds by lazy {
        UIRectangle(computePosition(), size)
    }

    @delegate:Transient
    private val size by lazy {
        computeSize()
    }

    @delegate:Transient
    private val fixedSize by lazy {
        sizes.firstOrNull()?.let { parseDimension(it.relDimension, it.absDimension) }
    }

    private fun computePosition(): UIPoint {
        return defaultTopLeftPosition ?: getPosition(AnchorPoint.TOPLEFT)
    }

    private fun computeSize(): UIPoint {
        defaultSize?.let { return it }
        fixedSize?.let { return it }
        var width = computeWidthWithAnchors()
        var height = computeHeightWithAnchors()
        if (width != null && height != null) {
            return UIPoint(width, height)
        }
        val size = computeSizeUsingChildren()
            ?: computeIconSize()
        width = width ?: size.x
        height = height ?: size.y
        return UIPoint(width, height)
    }

    @delegate:Transient
    private val centerPosition: UIPoint? by lazy {
        getForcedPosition(AnchorPoint.CENTER)
    }

    @delegate:Transient
    private val topLeftPosition: UIPoint? by lazy {
        getForcedPosition(AnchorPoint.TOPLEFT)
    }

    @delegate:Transient
    private val topPosition: UIPoint? by lazy {
        getForcedPosition(AnchorPoint.TOP)
    }

    @delegate:Transient
    private val topRightPosition: UIPoint? by lazy {
        getForcedPosition(AnchorPoint.TOPRIGHT)
    }

    @delegate:Transient
    private val rightPosition: UIPoint? by lazy {
        getForcedPosition(AnchorPoint.RIGHT)
    }

    @delegate:Transient
    private val bottomRightPosition: UIPoint? by lazy {
        getForcedPosition(AnchorPoint.BOTTOMRIGHT)
    }

    @delegate:Transient
    private val bottomPosition: UIPoint? by lazy {
        getForcedPosition(AnchorPoint.BOTTOM)
    }

    @delegate:Transient
    private val bottomLeftPosition: UIPoint? by lazy {
        getForcedPosition(AnchorPoint.BOTTOMLEFT)
    }

    @delegate:Transient
    private val leftPosition: UIPoint? by lazy {
        getForcedPosition(AnchorPoint.LEFT)
    }

    private fun getForcedPosition(anchorPoint: AnchorPoint): UIPoint? {
        val anchor = getAnchor(anchorPoint) ?: return null
        val anchorSize = parseDimension(anchor.relDimension, anchor.absDimension)
        val relativeContainer = getRelativeContainer(anchor)
        val relativePoint = AnchorPoint.valueOf(anchor.relativePoint)
        val relativePosition = relativeContainer.getPosition(relativePoint)
        return relativePosition.transpose(anchorSize)
    }

    private fun parseDimension(relDimension: Dimension, absDimension: Dimension): UIPoint {
        val x = if (relDimension.x.isNotEmpty()) {
            relDimension.x.toFloat() * parentContainer.size.x
        } else absDimension.x.toFloatOrNull() ?: 0.0f
        val y = if (relDimension.y.isNotEmpty()) {
            relDimension.y.toFloat() * parentContainer.size.y
        } else absDimension.y.toFloatOrNull() ?: 0.0f
        return UIPoint(x, y)
    }

    private fun getAnchor(anchorPoint: AnchorPoint): Anchor? {
        val validAnchors = anchors.filter { AnchorPoint.valueOf(it.point) == anchorPoint }
        if (validAnchors.size > 1) {
            error("Two anchor on the same point - $name (${root.name})")
        }
        return validAnchors.firstOrNull()
    }

    private fun getPosition(anchorPoint: AnchorPoint): UIPoint {
        val positionsByAnchorPoint = getNonNullPositionsByAnchorPoint()
        positionsByAnchorPoint[anchorPoint]?.let { return it }
        val refAnchorPointWithPosition = positionsByAnchorPoint.toList().firstOrNull()
            ?: (AnchorPoint.TOPLEFT to UIPoint())
        val refAnchorPoint = refAnchorPointWithPosition.first
        val refPosition = refAnchorPointWithPosition.second
        val widthRatio = refAnchorPoint.widthRatio - anchorPoint.widthRatio
        val heightRatio = refAnchorPoint.heightRatio - anchorPoint.heightRatio
        return UIPoint(refPosition.x - widthRatio * size.x, refPosition.y - heightRatio * size.y)
    }

    private fun computeWidthWithAnchors(): Float? {
        return computeSideWithAnchors({ it.widthRatio }, { it.x })
    }

    private fun computeHeightWithAnchors(): Float? {
        return computeSideWithAnchors({ it.heightRatio }, { it.y })
    }

    private fun computeSideWithAnchors(
        anchorRatioGetter: (AnchorPoint) -> Float, sideLengthGetter: (UIPoint) -> Float
    ): Float? {
        val positionsByAnchorPoint = getNonNullPositionsByAnchorPoint()
        val firstAnchorPointWithPosition = positionsByAnchorPoint.toList().firstOrNull() ?: return null
        val firstAnchorPoint = firstAnchorPointWithPosition.first
        val firstPosition = firstAnchorPointWithPosition.second
        val secondAnchorPointWithPosition = positionsByAnchorPoint.toList().firstOrNull {
            anchorRatioGetter(it.first) != anchorRatioGetter(firstAnchorPoint)
        } ?: return null
        val secondAnchorPoint = secondAnchorPointWithPosition.first
        val secondPosition = secondAnchorPointWithPosition.second
        val deltaRatio = anchorRatioGetter(firstAnchorPoint) - anchorRatioGetter(secondAnchorPoint)
        return (sideLengthGetter(firstPosition) - sideLengthGetter(secondPosition)) / deltaRatio
    }

    private fun computeSizeUsingChildren(): UIPoint? {
        val sizes = children.map { computeChildSize(it) }
        val childMaxWidth = sizes.maxOfOrNull { it.x } ?: return null
        val childMaxHeight = sizes.maxOfOrNull { it.y } ?: return null
        return UIPoint(childMaxWidth, childMaxHeight)
    }

    private fun computeChildSize(child: Container): UIPoint {
        val childSize = child.fixedSize
            ?: child.computeSizeUsingChildren()
            ?: child.computeIconSize()
        val marginLeft = child.anchors.firstOrNull { AnchorPoint.valueOf(it.relativePoint).widthRatio == 0f }?.let {
            val anchorPoint = AnchorPoint.valueOf(it.point)
            val margin = parseDimension(it.relDimension, it.absDimension).x.absoluteValue
            val widthDelta = anchorPoint.widthRatio * childSize.x
            margin - widthDelta
        } ?: 0.0f
        val marginRight = child.anchors.firstOrNull { AnchorPoint.valueOf(it.relativePoint).widthRatio == 1f }?.let {
            val anchorPoint = AnchorPoint.valueOf(it.point)
            val margin = parseDimension(it.relDimension, it.absDimension).x.absoluteValue
            val widthDelta = (1f - anchorPoint.widthRatio) * childSize.x
            margin - widthDelta
        } ?: 0.0f
        val marginTop = child.anchors.firstOrNull { AnchorPoint.valueOf(it.relativePoint).heightRatio == 0f }?.let {
            val anchorPoint = AnchorPoint.valueOf(it.point)
            val margin = parseDimension(it.relDimension, it.absDimension).y.absoluteValue
            val heightDelta = anchorPoint.heightRatio * childSize.y
            margin - heightDelta
        } ?: 0.0f
        val marginBottom = child.anchors.firstOrNull { AnchorPoint.valueOf(it.relativePoint).heightRatio == 1f }?.let {
            val anchorPoint = AnchorPoint.valueOf(it.point)
            val margin = parseDimension(it.relDimension, it.absDimension).y.absoluteValue
            val heightDelta = (1f - anchorPoint.heightRatio) * childSize.y
            margin - heightDelta
        } ?: 0.0f
        return UIPoint(childSize.x + marginLeft + marginRight, childSize.y + marginBottom + marginTop)
    }

    private fun computeIconSize(): UIPoint {
        return UIIconManager.getIconSize(this)
            ?: UIPoint(24f, 24f)
    }

    private fun getNonNullPositionsByAnchorPoint(): Map<AnchorPoint, UIPoint> {
        return listOf(
            AnchorPoint.CENTER to centerPosition,
            AnchorPoint.TOPLEFT to topLeftPosition,
            AnchorPoint.TOP to topPosition,
            AnchorPoint.TOPRIGHT to topRightPosition,
            AnchorPoint.RIGHT to rightPosition,
            AnchorPoint.BOTTOMRIGHT to bottomRightPosition,
            AnchorPoint.BOTTOM to bottomPosition,
            AnchorPoint.BOTTOMLEFT to bottomLeftPosition,
            AnchorPoint.LEFT to leftPosition
        ).mapNotNull { (k, v) -> if (v == null) null else k to v }.toMap()
    }

    private fun getRelativeContainer(anchor: Anchor): Container {
        return if (anchor.relativeTo.isNotEmpty()) {
            root.findContainer(anchor.relativeTo) ?: error("Missing container : ${anchor.relativeTo}")
        } else parentContainer
    }

    fun findContainer(name: String): Container? {
        if (this.name == name) {
            return this
        }
        for (container in children) {
            container.findContainer(name)?.let { return it }
        }
        return null
    }

    fun initRoot(root: Container) {
        this.root = root
        children.forEach { it.initRoot(root) }
    }

    fun deepCopy(): Container {
        return Container(
            name,
            ArrayList(sizes.map { it.deepCopy() }),
            ArrayList(anchors.map { it.deepCopy() }),
            uri,
            themeDataId
        ).also { copy -> copy.children.addAll(children.map { child -> child.deepCopy() }) }
    }

}