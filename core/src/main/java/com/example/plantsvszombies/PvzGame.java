package com.example.plantsvszombies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.managers.ScreenManager;
import com.example.plantsvszombies.managers.SoundManager;
import com.example.plantsvszombies.screens.LoadingScreen;

public class PvzGame extends Game {
    // 全局唯一的SpriteBatch，所有屏幕共享，避免重复创建
    public SpriteBatch batch;
    // 全局唯一的资源管理器
    public AssetMan assetManager;

    public SoundManager soundManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = AssetMan.getInstance();
        soundManager = SoundManager.getInstance();

        // 资源加载完毕后，设置并切换到游戏主屏幕
        setScreen(new LoadingScreen(this));
    }

    @Override
    public void render() {
        // Game基类会自动调用当前Screen的render()方法
        super.render();
    }

    @Override
    public void dispose() {
        // 游戏退出时，释放所有资源
        if (batch != null) { batch.dispose(); }
        if (assetManager != null) { assetManager.dispose(); }
        if (soundManager != null) { soundManager.dispose(); }
        super.dispose();
    }
}
