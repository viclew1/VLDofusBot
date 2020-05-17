package fr.lewon.dofus.bot.json

import com.fasterxml.jackson.annotation.JsonProperty
import fr.lewon.dofus.bot.util.Directions
import java.awt.GraphicsEnvironment

data class DTBConfig(
    @field:JsonProperty var huntLevel: Int = 200,
    @field:JsonProperty var world: String = "Main",
    @field:JsonProperty var autopilot: Boolean = true,
    @field:JsonProperty var gameScreenRegion: String = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.iDstring,
    @field:JsonProperty var moveTimeout: Int = 25,
    @field:JsonProperty var leftAccessPos: DTBPoint = DTBPoint(329, 129),
    @field:JsonProperty var rightAccessPos: DTBPoint = DTBPoint(1593, 141),
    @field:JsonProperty var bottomAccessPos: DTBPoint = DTBPoint(1290, 924),
    @field:JsonProperty var topAccessPos: DTBPoint = DTBPoint(1555, 25),
    @field:JsonProperty var mouseRestPos: DTBPoint = DTBPoint(1874, 38),
    @field:JsonProperty var registeredMoveLocationsByMap: RegisteredMovesHolder = RegisteredMovesHolder()

)

class RegisteredMovesHolder : HashMap<String, PositionsByDirection>()

class PositionsByDirection : HashMap<Directions, DTBPoint>()

data class DTBPoint(
    @field:JsonProperty var first: Int = -1,
    @field:JsonProperty var second: Int = -1
)