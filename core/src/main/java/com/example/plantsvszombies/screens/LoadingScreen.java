// java
package com.example.plantsvszombies.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.example.plantsvszombies.PvzGame;
import com.example.plantsvszombies.Resource.AssetMusic;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;

public class LoadingScreen implements Screen {

    private final PvzGame game;
    private final float MIN_DISPLAY_TIME = 2.0f;
    private float elapsedTime = 0f;
    private boolean assetsLoaded = false;
    private final AssetMan assetMan;

    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 600f;

    private final FitViewport viewport;
    private final OrthographicCamera camera;

    public LoadingScreen(PvzGame game) {
        this.game = game;
        this.assetMan = game.assetManager;

        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0f);

        // 关键：进入画面立刻用当前窗口尺寸更新并居中（避免首帧贴边）
        camera.update();
    }

    @Override
    public void show() {
        game.bgMusic = AssetMusic.MAINTHEME.loadAndGet(assetMan);
        game.bgMusic.setLooping(true);
        game.bgMusic.setVolume(0.5f);
        game.bgMusic.play();
        assetMan.loadAssets();
        assetsLoaded = true;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.3f, 0.7f, 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        elapsedTime += delta;
        viewport.apply();
        camera.update();

        if (assetsLoaded && assetMan.getAssetManager().update() && elapsedTime >= MIN_DISPLAY_TIME) {
//            game.setScreen(new GameScreen(game));
            game.setScreen(new HomeScreen(game));
            return;
        }

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(AssetTexture.TITLE_SCREEN.get(assetMan), 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
