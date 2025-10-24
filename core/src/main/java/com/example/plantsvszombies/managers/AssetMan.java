package com.example.plantsvszombies.managers;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.example.plantsvszombies.Animation.AnimationClip;

import java.util.HashMap;
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
        assetManager.load("graphics/Map/map0.jpg", Texture.class);
        assetManager.load("graphics/Plants/Peashooter/0.gif", Texture.class);
        assetManager.load("graphics/Plants/Peashooter/Peashooter.png", Texture.class);
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

        assetManager.load("graphics/Screen/Sun.gif", Texture.class);
        assetManager.load("graphics/Screen/car.png", Texture.class);
    }

    public void loadAnimationAssets() {
        // 这里可以添加动画资源的加载逻辑
        loadAnimation("Peashooter", "graphics/Plants/Peashooter/Peashooter.png", 13, 1170);
        loadAnimation("SunFlower", "graphics/Plants/SunFlower/SunFlower.png", 18, 1980);
        loadAnimation("WallNut", "graphics/Plants/WallNut/WallNut.png", 16, 1920);
        loadAnimation("WallNut_Cracked1", "graphics/Plants/WallNut/Wallnut_cracked1.png", 11, 1100);
        loadAnimation("WallNut_Cracked2", "graphics/Plants/WallNut/Wallnut_cracked2.png", 15, 1500);

        loadAnimation("NormalZombie", "graphics/Zombies/Zombie/Zombie.png", 22, 2970);
        loadAnimation("NormalZombieAttack", "graphics/Zombies/Zombie/ZombieAttack.png", 21, 2150);
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

    public AnimationClip getAnimation(String key) {
        return animations.get(key);
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
