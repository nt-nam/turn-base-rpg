package com.game.managers;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.game.MainGame;

public class AudioManager {
    private Music currentMusic;

    public void playMusic(String path, boolean loop) {
        if (currentMusic != null) {
            currentMusic.stop();
        }
        currentMusic = MainGame.getAsM().getMusic(path);
        currentMusic.setLooping(loop);
        currentMusic.play();
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }

    public void setMusicVolume(float volume) {
        if (currentMusic != null) {
            currentMusic.setVolume(volume);
        }
    }

    public void playSound(String path) {
        Sound sound = MainGame.getAsM().getSound(path);
        sound.play();
    }
}
