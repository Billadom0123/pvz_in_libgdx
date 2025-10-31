package com.example.plantsvszombies.screens;

import com.badlogic.gdx.*;
import com.example.plantsvszombies.PvzGame;

public class HomeScreen implements Screen {
    private final PvzGame game;

    public HomeScreen(PvzGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        // 设置输入处理器
//        Gdx.input.setInputProcessor(new InputAdapter() {
//            @Override
//            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//                // 点击事件处理，切换到目标屏幕
//                game.setScreen(new GameScreen(game));
//                return true;
//            }
//        });
    }

    @Override
    public void render(float delta) {
        game.batch.begin();
        // 绘制主页内容
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        // 释放资源
    }
}
