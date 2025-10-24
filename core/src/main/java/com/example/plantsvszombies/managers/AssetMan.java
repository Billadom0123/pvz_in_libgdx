package com.example.plantsvszombies.managers;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class AssetMan {
    private static AssetMan instance;
    private final AssetManager assetManager;

    private AssetMan() {
        assetManager = new AssetManager();
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
        assetManager.load("graphics/Plants/SunFlower/0.gif", Texture.class);
        assetManager.load("graphics/Zombies/Zombie/Zombie.gif", Texture.class);
        assetManager.load("graphics/Bullets/PeaNormal/PeaNormal_0.png", Texture.class);
        assetManager.load("graphics/Screen/Sun.gif", Texture.class);
        assetManager.load("graphics/Cards/card_peashooter.png", Texture.class);
        assetManager.load("graphics/Cards/card_sunflower.png", Texture.class);
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
    }
}
