package fr.lewon.dofus.bot.core.ui.managers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.core.ui.UIPoint
import fr.lewon.dofus.bot.core.ui.xml.containers.Container
import java.io.File
import javax.imageio.ImageIO

object UIIconManager {

    private val objectMapper = ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
        .enable(JsonParser.Feature.ALLOW_COMMENTS)
    private val sizeByIconName = HashMap<String, UIPoint>()
    private val globalThemeData = ThemeData()

    fun initIcon(iconFile: File) {
        val image = ImageIO.read(iconFile)
        sizeByIconName[iconFile.nameWithoutExtension] = UIPoint(image.width.toFloat(), image.height.toFloat())
    }

    fun initThemeData(themeDataFile: File) {
        val themeData: ThemeData = objectMapper.readValue(themeDataFile)
        globalThemeData.putAll(themeData)
    }

    fun getIconSize(container: Container): UIPoint? {
        val iconName = container.uri
        if (iconName.isNotEmpty() && iconName != "null") {
            return getIconSize(iconName)
        }
        val themeId = container.themeDataId
        if (themeId.isNotEmpty() && themeId != "null") {
            val theme = globalThemeData[themeId] ?: return null
            if (theme.scale9Grid.width != 0 && theme.scale9Grid.height != 0) {
                return UIPoint(
                    (theme.scale9Grid.x * 2 + theme.scale9Grid.width).toFloat(),
                    (theme.scale9Grid.y * 2 + theme.scale9Grid.height).toFloat()
                )
            }
            return getIconSize(theme.uri)
        }
        return null
    }

    private fun getIconSize(iconName: String): UIPoint? {
        val lastSlashIndex = iconName.lastIndexOf("/")
        val iconNameWithoutPath = if (lastSlashIndex > 0) {
            iconName.substring(lastSlashIndex + 1)
        } else iconName
        val firstDotIndex = iconNameWithoutPath.indexOf(".")
        val iconNameWithoutExtension = if (firstDotIndex > 0) {
            iconNameWithoutPath.substring(0, firstDotIndex)
        } else iconNameWithoutPath
        return sizeByIconName[iconNameWithoutExtension]
    }

    private class ThemeData : HashMap<String, Theme>()

    private data class Theme(
        var uri: String = "",
        var scale9Grid: ScaleGrid = ScaleGrid()
    )

    private data class ScaleGrid(
        var x: Int = 0,
        var y: Int = 0,
        var width: Int = 0,
        var height: Int = 0,
    )
}