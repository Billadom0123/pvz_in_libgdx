package com.example.plantsvszombies.managers;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.example.plantsvszombies.AssetImpl.SoundInstance;
import com.example.plantsvszombies.Resource.AssetMusic;
import com.example.plantsvszombies.Resource.AssetSound;

import java.util.*;

public class SoundManager {

    private static SoundManager instance;
    private static AssetMan assetMan;

    private float musicVolume = 1f;
    private float sfxVolume = 1f;

    private float CLEAR_INTERVAL = 15f; // 秒
    private float timeSinceLastClear = 0f;

    // 正在播放的 Sound 实例 (同一个 Sound 可能多次播放, 每次一个 id)
    private final Map<Sound, List<SoundInstance>> activeSoundIds = new HashMap<>();
    // 正在播放的 Music
    private Music activeMusic;

    private SoundManager() {
        assetMan = AssetMan.getInstance();
    }

    public static synchronized SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void update(float delta) {
        timeSinceLastClear += delta;
        if (timeSinceLastClear >= CLEAR_INTERVAL) {
            clearInactive();
            timeSinceLastClear = 0f;
        }
    }

    public long playSound(Sound sound) {
        long id = sound.play(sfxVolume);
        activeSoundIds.computeIfAbsent(sound, k -> new ArrayList<>()).add(new SoundInstance(id));
        return id;
    }

    public long playSound(AssetSound assetSound) {
        Sound s = assetSound.get(assetMan);
        return playSound(s);
    }

    public void playRandomSound(List<AssetSound> sounds) {
        if (sounds == null || sounds.isEmpty()) return;
        int index = MathUtils.random(sounds.size() - 1);
        playSound(sounds.get(index));
    }

    public void stopSound(Sound sound) {
        List<SoundInstance> ids = activeSoundIds.remove(sound);
        if (ids != null) {
            for (SoundInstance id : ids) {
                sound.stop(id.getId());
            }
        }
    }

    public Long loopSound(Sound sound) {
        long id = sound.loop(sfxVolume);
        activeSoundIds.computeIfAbsent(sound, k -> new ArrayList<>()).add(new SoundInstance(id, true));
        return id;
    }

    public Long loopSound(AssetSound assetSound) {
        Sound s = assetSound.get(assetMan);
        return loopSound(s);
    }

    public void pauseAllSound() {
        for (Map.Entry<Sound, List<SoundInstance>> e : activeSoundIds.entrySet()) {
            Sound s = e.getKey();
            for (SoundInstance id : e.getValue()) {
                s.pause(id.getId());
            }
        }
    }

    public void resumeAllSound() {
        for (Map.Entry<Sound, List<SoundInstance>> e : activeSoundIds.entrySet()) {
            Sound s = e.getKey();
            for (SoundInstance id : e.getValue()) {
                s.resume(id.getId());
            }
        }
    }

    public void stopSoundInstance(Sound sound, long id) {
        List<SoundInstance> ids = activeSoundIds.get(sound);
        if (ids != null && ids.removeIf(i -> i.getId() == id)) {
            sound.stop(id);
            if (ids.isEmpty()) activeSoundIds.remove(sound);
        }
    }

    public void stopSoundInstance(AssetSound assetSound, long id) {
        Sound s = assetSound.get(assetMan);
        stopSoundInstance(s, id);
    }

    public void changeMusic(Music music, boolean looping) {
        if (music == activeMusic) return;
        playMusic(music, looping);
    }

    public void changeMusic(AssetMusic assetMusic, boolean looping) {
        Music music = assetMusic.get(assetMan);
        changeMusic(music, looping);
    }

    public void playMusic(Music music, boolean looping) {
        if (activeMusic != null) {
            activeMusic.stop();
        }
        activeMusic = music;
        music.setLooping(looping);
        music.setVolume(musicVolume);
        music.play();
    }

    public void playMusic(AssetMusic assetMusic, boolean looping) {
        playMusic(assetMusic.get(assetMan), looping);
    }

    public void pauseMusic() {
        if (activeMusic != null) {
            activeMusic.pause();
        }
    }

    public void resumeMusic() {
        if (activeMusic != null) {
            activeMusic.play();
        }
    }

    public void replayMusic() {
        if (activeMusic != null) {
            activeMusic.stop();
            activeMusic.play();
        }
    }

    public void pauseSoundsAndMusic() {
        pauseAllSound();
        pauseMusic();
    }

    public void resumeSoundsAndMusic() {
        resumeAllSound();
        resumeMusic();
    }

    public void stopMusic() {
        if (activeMusic != null) {
            activeMusic.stop();
            activeMusic = null;
        }
    }

    public void stopAll() {
        // 停所有 Sound
        for (Map.Entry<Sound, List<SoundInstance>> e : activeSoundIds.entrySet()) {
            Sound s = e.getKey();
            for (SoundInstance id : e.getValue()) {
                s.stop(id.getId());
            }
        }
        activeSoundIds.clear();
        // 停 Music
        if (activeMusic != null) {
            activeMusic.stop();
            activeMusic = null;
        }
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = MathUtils.clamp(musicVolume, 0f, 1f);
        // 立即更新所有 Music
        if (activeMusic != null) {
            activeMusic.setVolume(this.musicVolume);
        }
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public void setSfxVolume(float sfxVolume) {
        this.sfxVolume = MathUtils.clamp(sfxVolume, 0f, 1f);
        // 重新设置所有仍被追踪的 Sound 实例音量
        for (Map.Entry<Sound, List<SoundInstance>> e : activeSoundIds.entrySet()) {
            Sound s = e.getKey();
            for (SoundInstance id : e.getValue()) {
                s.setVolume(id.getId(), this.sfxVolume); // 若需要每次播放的相对音量, 可再维护一个并乘
            }
        }
    }

    // 可选: 清理失效实例(无法准确判断是否结束, 通常忽略或有节奏地 stop)
    public void clearInactive() {
        for (Iterator<Map.Entry<Sound, List<SoundInstance>>> it = activeSoundIds.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Sound, List<SoundInstance>> entry = it.next();
            List<SoundInstance> ids = entry.getValue();
            Long now = System.currentTimeMillis();
            ids.removeIf(id -> id.isFinished(now, 15000L));
            if (ids.isEmpty()) {
                it.remove();
            }
        }
    }

    public void dispose() {
        stopAll();
    }
}
