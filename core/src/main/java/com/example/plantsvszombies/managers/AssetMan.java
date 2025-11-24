package com.example.plantsvszombies.managers;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.example.plantsvszombies.AssetImpl.AnimationClip;
import com.example.plantsvszombies.Resource.*;

import java.util.HashMap;
import java.util.Map;

public class AssetMan {
    private static AssetMan instance;
    private final AssetManager assetManager;
    private final GameManager gameManager;
    private final Map<String, AnimationClip> animations;
    private final Map<String, ParticleEffect> particleEffects = new HashMap<>();
    private final Map<String, BitmapFont> fonts = new HashMap<>();


    private AssetMan() {
        assetManager = new AssetManager();
        gameManager = GameManager.getInstance();
        animations = new HashMap<>();
    }

    public static AssetMan getInstance() {
        if (instance == null) {
            instance = new AssetMan();
        }
        return instance;
    }

    /**
     * 将所有需要的资源加入加载队列
     */
    public void loadAssets() {
        AssetTexture.loadAll(this);
        AssetSound.loadAll(this);
        AssetMusic.loadAll(this);
        AssetParticle.loadAll(this);
        assetManager.finishLoading();
        AssetAnimation.loadAll(this);
        AssetFont.loadAll(this);
        AssetSkin.loadAll(this);
    }

    public void loadTexture(String filePath) { assetManager.load(filePath, Texture.class); }

    public Texture getTexture(String filePath) { return assetManager.get(filePath, Texture.class); }

    public void loadFont(String key, String filepath, FreeTypeFontGenerator.FreeTypeFontParameter parameter) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(filepath));
        BitmapFont font = generator.generateFont(parameter);
//        font.getData().setLineHeight(font.getLineHeight() + 5f);
        fonts.put(key, font);
        generator.dispose();
    }

    public BitmapFont getFont(String key) {
        return fonts.get(key);
    }

    public void loadSkin(String filepath) {
        assetManager.load(filepath, Skin.class);
    }

    public Skin getSkin(String filepath) {
        return assetManager.get(filepath, Skin.class);
    }

    public void loadSound(String filePath) { assetManager.load(filePath, Sound.class); }

    public Sound getSound(String filePath) { return assetManager.get(filePath, Sound.class); }

    public void loadMusic(String filePath) { assetManager.load(filePath, Music.class); }

    public Music getMusic(String filePath) { return assetManager.get(filePath, Music.class); }

    public void loadAnimation(String key, String filepath, int frames, float duration) {
        Texture texture = assetManager.get(filepath, Texture.class);
        if (duration >= 10) {
            duration /= 1000f; // 转换为秒
        }
        AnimationClip animationClip = new AnimationClip(texture, frames, duration);
        animations.put(key, animationClip);
    }

    public AnimationClip getAnimation(String key) {
        return animations.get(key);
    }

    public void loadParticleEffect(String key, String effectFilePath, String imagesDir) {
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal(effectFilePath), Gdx.files.internal(imagesDir));
        particleEffects.put(key, particleEffect);
    }

    public ParticleEffect getParticleEffect(String key) {
        ParticleEffect original = particleEffects.get(key);
        if (original != null) {
            return new ParticleEffect(original); // 返回副本以避免状态冲突
        }
        return null;
    }

//    public void playSound(AssetSound sound, float volume) {
//        Sound s = sound.get(this);
//        if (s != null) {
//            s.play(volume);
//        }
//    }
//
//    public void playRandomSound(List<AssetSound> sounds, float volume) {
//        if (sounds.isEmpty()) return;
//        int index = MathUtils.random(0, sounds.size() - 1);
//        playSound(sounds.get(index), volume);
//    }

    public void unloadAsset(String filePath) {
        if (assetManager.isLoaded(filePath)) {
            assetManager.unload(filePath);
        }
    }
    /**
     * 获取底层的AssetManager实例
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * 在游戏退出时释放所有已加载的资源
     */
    public void dispose() {
        assetManager.dispose();
        animations.clear();
        particleEffects.clear();
        fonts.clear();
    }
}
