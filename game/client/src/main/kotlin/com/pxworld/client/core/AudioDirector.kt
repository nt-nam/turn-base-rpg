package com.pxworld.client.core

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.Disposable
import com.pxworld.content.AudioCueRecord

class AudioDirector(cues: List<AudioCueRecord>, private val assetMap: Map<String, String>) : Disposable {

    private val manager = AssetManager()
    private val cues = cues.associateBy { it.id }
    private var current: Pair<String, Music>? = null
    var musicEnabled: Boolean = true
        private set
    var soundEnabled: Boolean = true
        private set

    val playingCue: String? get() = current?.first

    fun configure(music: Boolean, sound: Boolean) {
        soundEnabled = sound
        if (music == musicEnabled) return
        musicEnabled = music
        if (!music) current?.second?.pause() else current?.second?.play()
    }

    fun playMusic(cueId: String?) {
        if (cueId == current?.first) return
        current?.second?.stop()
        current = null
        val cue = cueId?.let { cues[it] } ?: return
        val music = load(cue, Music::class.java) ?: return
        music.isLooping = true
        music.volume = cue.volumePercent / 100f
        current = cueId to music
        if (musicEnabled) music.play()
    }

    fun playSound(cueId: String) {
        if (!soundEnabled) return
        val cue = cues[cueId] ?: return
        load(cue, Sound::class.java)?.play(cue.volumePercent / 100f)
    }

    private fun <T> load(cue: AudioCueRecord, type: Class<T>): T? {
        val path = assetMap[cue.asset] ?: return null
        return runCatching {
            if (!manager.isLoaded(path, type)) {
                manager.load(path, type)
                manager.finishLoadingAsset<T>(path)
            }
            manager.get(path, type)
        }.getOrNull()
    }

    override fun dispose() {
        current?.second?.stop()
        manager.dispose()
    }

    companion object {
        const val WORLD_MUSIC = "audio.music.world"
        const val BATTLE_MUSIC = "audio.music.battle"
        const val CLICK = "audio.sfx.click"
        const val HIT = "audio.sfx.hit"
        const val CRITICAL = "audio.sfx.critical"
    }
}
