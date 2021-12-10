package fr.lewon.dofus.bot.gui.sound

import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip


enum class SoundType(soundFileName: String) {

    RARE_MONSTER_FOUND("rare_monster_found.wav"),
    FAILED("failed.wav");

    private val clip: Clip

    init {
        val ais = AudioSystem.getAudioInputStream(javaClass.getResourceAsStream("/sounds/$soundFileName"))
        clip = AudioSystem.getClip()
        clip.open(ais)
    }

    fun playSound() {
        clip.microsecondPosition = 0
        clip.start()
    }

}