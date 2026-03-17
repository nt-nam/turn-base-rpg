package com.game.managers;

import static com.game.utils.Constants.MUSIC_WORLD1;
import static com.game.utils.Constants.SOUND_BUBBLE_SHOOT;
import static com.game.utils.Constants.SOUND_BUBBLE_SWITCH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.game.models.entity.Profile;

import java.util.HashMap;
import java.util.Map;

public class SoundManager {
  private Music backgroundMusic;
  private Map<String, Sound> soundEffects;
  private float musicVolume;
  private float soundVolume;
  private boolean musicMuted;
  private boolean soundMuted;

  public SoundManager() {
    this.soundEffects = new HashMap<>();
    this.musicVolume = 1f;
    this.soundVolume = 1f;
    this.musicMuted = false;
    this.soundMuted = false;
    loadSounds();
  }

  // Tải âm thanh
  private void loadSounds() {
    backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(MUSIC_WORLD1));
    backgroundMusic.setLooping(true);
    backgroundMusic.setVolume(musicMuted ? 0 : musicVolume);

    soundEffects.put("switch", Gdx.audio.newSound(Gdx.files.internal(SOUND_BUBBLE_SWITCH)));
    soundEffects.put("click", Gdx.audio.newSound(Gdx.files.internal(SOUND_BUBBLE_SHOOT)));
  }

  // Khởi tạo trạng thái từ Player
  public void initFromPlayer(Profile player) {
    this.musicMuted = player.playMusic;
    this.soundMuted = player.playSound;
    updateMusicState();
  }

  public void changeBackgroundMusic(String path){
      backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(path));
      backgroundMusic.setLooping(true);
      backgroundMusic.setVolume(musicMuted ? 0 : musicVolume);
  }

  // Phát nhạc nền
  public void playBackgroundMusic() {
    if (!musicMuted && !backgroundMusic.isPlaying()) {
      backgroundMusic.play();
    }
  }

  // Dừng nhạc nền
  public void stopBackgroundMusic() {
    if (backgroundMusic.isPlaying()) {
      backgroundMusic.stop();
    }
  }

  // Phát hiệu ứng âm thanh
  public void playSound(String soundName) {
    if (!soundMuted) {
      Sound sound = soundEffects.get(soundName);
      if (sound != null) {
        sound.play(soundVolume);
      } else {
        Gdx.app.log("SoundManager", "Sound not found: " + soundName);
      }
    }
  }

  // Điều chỉnh âm lượng nhạc nền
  public void setMusicVolume(float volume) {
    if (volume >= 0f && volume <= 1f) {
      this.musicVolume = volume;
      if (!musicMuted) {
        backgroundMusic.setVolume(volume);
      }
    }
  }

  // Điều chỉnh âm lượng hiệu ứng âm thanh
  public void setSoundVolume(float volume) {
    if (volume >= 0f && volume <= 1f) {
      this.soundVolume = volume;
    }
  }

  // Bật/tắt nhạc nền
  public void setMusicMuted(boolean muted) {
    this.musicMuted = muted;
    updateMusicState();
  }

  // Cập nhật trạng thái nhạc
  private void updateMusicState() {
    backgroundMusic.setVolume(musicMuted ? 0 : musicVolume);
    if (musicMuted) {
      stopBackgroundMusic();
    } else {
      playBackgroundMusic();
    }
  }

  // Bật/tắt hiệu ứng âm thanh
  public void setSoundMuted(boolean muted) {
    this.soundMuted = muted;
  }

  // Getter
  public float getMusicVolume() {
    return musicVolume;
  }

  public float getSoundVolume() {
    return soundVolume;
  }

  public boolean isMusicMuted() {
    return musicMuted;
  }

  public boolean isSoundMuted() {
    return soundMuted;
  }

  // Giải phóng tài nguyên
  public void dispose() {
    if (backgroundMusic != null) {
      backgroundMusic.dispose();
    }
    for (Sound sound : soundEffects.values()) {
      sound.dispose();
    }
    soundEffects.clear();
  }
}
