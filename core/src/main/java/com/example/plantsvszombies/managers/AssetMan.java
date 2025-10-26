package com.example.plantsvszombies.managers;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.example.plantsvszombies.Animation.AnimationClip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetMan {
    private static AssetMan instance;
    private final AssetManager assetManager;
    private final Map<String, AnimationClip> animations;

    private AssetMan() {
        assetManager = new AssetManager();
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
        loadTextureAssets();
        loadSoundAssets();
        loadMusicAssets();
        assetManager.finishLoading();
        loadAnimationAssets();
    }

    private void loadTextureAssets() {
        assetManager.load("graphics/Map/map0.jpg", Texture.class);
        assetManager.load("graphics/Plants/Peashooter/0.gif", Texture.class);
        assetManager.load("graphics/Plants/Peashooter/Peashooter.png", Texture.class);
        assetManager.load("graphics/Bullets/PeaNormalExplode/pea_splats.png", Texture.class);
        assetManager.load("graphics/Plants/SunFlower/0.gif", Texture.class);
        assetManager.load("graphics/Plants/SunFlower/SunFlower.png", Texture.class);
        assetManager.load("graphics/Plants/WallNut/0.gif", Texture.class);
        assetManager.load("graphics/Plants/WallNut/WallNut.png", Texture.class);
        assetManager.load("graphics/Plants/WallNut/Wallnut_cracked1.png", Texture.class);
        assetManager.load("graphics/Plants/WallNut/Wallnut_cracked2.png", Texture.class);

        assetManager.load("graphics/Zombies/Zombie/Zombie.gif", Texture.class);
        assetManager.load("graphics/Zombies/Zombie/Zombie.png", Texture.class);
        assetManager.load("graphics/Zombies/Zombie/ZombieAttack.png", Texture.class);

        assetManager.load("graphics/Bullets/PeaNormal/PeaNormal_0.png", Texture.class);

        assetManager.load("graphics/Cards/card_peashooter.png", Texture.class);
        assetManager.load("graphics/Cards/card_sunflower.png", Texture.class);
        assetManager.load("graphics/Cards/card_wallnut.png", Texture.class);

        assetManager.load("graphics/Screen/SeedBank.png", Texture.class);
        assetManager.load("graphics/Screen/Sun.gif", Texture.class);
        assetManager.load("graphics/Screen/Sun.png", Texture.class);
        assetManager.load("graphics/Screen/car.png", Texture.class);
    }

    private void loadSoundAssets() {
        // 这里可以添加音频资源的加载逻辑
        assetManager.load("graphics/audio/seedlift.ogg", Sound.class); // 从顶部选植物的音效
        assetManager.load("graphics/audio/buzzer.ogg", Sound.class);   // 无法选择植物的音效
        assetManager.load("graphics/audio/points.ogg", Sound.class);    // 收集阳光的音效
        assetManager.load("graphics/audio/plant1.ogg", Sound.class);     // 种植植物的音效1
        assetManager.load("graphics/audio/plant2.ogg", Sound.class);     // 种植植物的音效2
        assetManager.load("graphics/audio/chomp.ogg", Sound.class);    // 僵尸吃植物的音效1
        assetManager.load("graphics/audio/chompsoft.ogg", Sound.class);   // 僵尸吃植物的音效2
        assetManager.load("graphics/audio/awooga.ogg", Sound.class);    // 第一只僵尸出现的音效
        assetManager.load("graphics/audio/lawnmower.ogg", Sound.class);    // 小推车的音效
        assetManager.load("graphics/audio/throw.ogg", Sound.class);    // 射豌豆的音效
        assetManager.load("graphics/audio/splat2.ogg", Sound.class); // 豌豆攻击普通僵尸的音效
    }

    private void loadMusicAssets() {
        // 这里可以添加音乐资源的加载逻辑
        assetManager.load("graphics/audio/UraniwaNi.ogg", Music.class); // 背景音乐
    }

    private void loadAnimationAssets() {
        // 这里可以添加动画资源的加载逻辑
        loadAnimation("Peashooter", "graphics/Plants/Peashooter/Peashooter.png", 13, 1170);
        loadAnimation("PeaSplat", "graphics/Bullets/PeaNormalExplode/pea_splats.png", 4, 200);
        loadAnimation("SunFlower", "graphics/Plants/SunFlower/SunFlower.png", 18, 1980);
        loadAnimation("WallNut", "graphics/Plants/WallNut/WallNut.png", 16, 1920);
        loadAnimation("WallNut_Cracked1", "graphics/Plants/WallNut/Wallnut_cracked1.png", 11, 1100);
        loadAnimation("WallNut_Cracked2", "graphics/Plants/WallNut/Wallnut_cracked2.png", 15, 1500);

        loadAnimation("NormalZombie", "graphics/Zombies/Zombie/Zombie.png", 22, 2970);
        loadAnimation("NormalZombieAttack", "graphics/Zombies/Zombie/ZombieAttack.png", 21, 2150);

        loadAnimation("Sun", "graphics/Screen/Sun.png", 29, 2900);
    }

    public void loadAnimation(String key, String filepath, int frames, float duration) {
        Texture texture = assetManager.get(filepath, Texture.class);
        if (duration >= 10) {
            duration /= 1000f; // 转换为秒
        }
        AnimationClip animationClip = new AnimationClip(texture, frames, duration);
        animations.put(key, animationClip);
    }

    public Texture getTexture(String filePath) {
        return assetManager.get(filePath, Texture.class);
    }

    public Sound getSound(String filePath) {
        return assetManager.get(filePath, Sound.class);
    }

    public void playSound(String filePath, float volume) {
        Sound sound = getSound(filePath);
        if (sound != null) {
            sound.play(volume);
        }
    }

    public void playSound(String filePath) {
        playSound(filePath, 1.0f);
    }

    public void playRandomSound(List<String> filePaths, float volume) {
        if (filePaths.isEmpty()) return;
        int index = (int) (Math.random() * filePaths.size());
        playSound(filePaths.get(index), volume);
    }

    public void playRandomSound(List<String> filePaths) {
        playRandomSound(filePaths, 1.0f);
    }

    public Music getMusic(String filePath) {
        return assetManager.get(filePath, Music.class);
    }

    public AnimationClip getAnimation(String key) {
        return animations.get(key);
    }


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
    }
}
