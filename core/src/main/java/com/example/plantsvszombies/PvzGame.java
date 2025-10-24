package com.example.plantsvszombies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.screens.GameScreen;

public class PvzGame extends Game {
    // 全局唯一的SpriteBatch，所有屏幕共享，避免重复创建
    public SpriteBatch batch;
    // 全局唯一的资源管理器
    public AssetMan assetManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = AssetMan.getInstance();

        // 开始加载资源
        assetManager.loadAssets();
        // 同步阻塞，直到所有资源加载完成
        assetManager.getAssetManager().finishLoading();

        assetManager.loadAnimationAssets();

        // 资源加载完毕后，设置并切换到游戏主屏幕
        setScreen(new GameScreen(this));
    }

    @Override
    public void render() {
        // Game基类会自动调用当前Screen的render()方法
        super.render();
    }

    @Override
    public void dispose() {
        // 游戏退出时，释放所有资源
        batch.dispose();
        assetManager.dispose();
    }
}
