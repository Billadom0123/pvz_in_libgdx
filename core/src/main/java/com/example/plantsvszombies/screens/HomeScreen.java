package com.example.plantsvszombies.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.example.plantsvszombies.PvzGame;
import com.example.plantsvszombies.Resource.AssetSound;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.ui.HomePageButton;

public class HomeScreen implements Screen {
    private final PvzGame game;

    private float elapsedTime = 0f;
    private final AssetMan assetMan;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final Stage stage;


    private static final float WORLD_WIDTH = 900f;
    private static final float WORLD_HEIGHT = 600f;

    private final FitViewport viewport;
    private final OrthographicCamera camera;

    public HomeScreen(PvzGame game) {
        this.game = game;
        this.assetMan = game.assetManager;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        camera.position.set(WORLD_HEIGHT / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        stage = new Stage(viewport, game.batch);

        // 创建开始冒险按钮
        HomePageButton startAdventureButton = new HomePageButton(AssetTexture.START_ADVENTURE_BUTTON.get(assetMan),
            AssetTexture.START_ADVENTURE_HIGHLIGHT.get(assetMan),
            null, AssetSound.BLEEP.get(assetMan),
            new Polygon(new float[]{0, 50, 5, 130, 70, 130, 100, 150, 240, 150, 280, 110, 360, 100, 345, 0, 0, 50}));
        startAdventureButton.setPosition(460, 390);
        startAdventureButton.setTouchable(Touchable.enabled);
        startAdventureButton.addListener(l ->{
            if (l instanceof InputEvent event && event.getType() == InputEvent.Type.touchDown) {
                game.setScreen(new GameScreen(game));
                return true;
            }
            return false;
        });
        stage.addActor(startAdventureButton);

        HomePageButton miniGameButton = new HomePageButton(AssetTexture.MINI_GAME_BUTTON.get(assetMan),
            AssetTexture.MINI_GAME_HIGHLIGHT.get(assetMan),
            null, AssetSound.BLEEP.get(assetMan),
            new Polygon(new float[]{5, 65, 5, 135, 340, 90, 325, 5}));
        miniGameButton.setPosition(460, 295);
        miniGameButton.setTouchable(Touchable.enabled);
        stage.addActor(miniGameButton);

        HomePageButton puzzleButton = new HomePageButton(AssetTexture.PUZZLE_BUTTON.get(assetMan),
            AssetTexture.PUZZLE_HIGHLIGHT.get(assetMan),
            null, AssetSound.BLEEP.get(assetMan),
            new Polygon(new float[]{5, 65, 5, 130, 310, 80, 295, 5}));
        puzzleButton.setPosition(465, 218);
        puzzleButton.setTouchable(Touchable.enabled);
        stage.addActor(puzzleButton);

        HomePageButton challengeButton = new HomePageButton(AssetTexture.CHALLENGE_BUTTON.get(assetMan),
            AssetTexture.CHALLENGE_HIGHLIGHT.get(assetMan),
            null, AssetSound.BLEEP.get(assetMan),
            new Polygon(new float[]{10, 70, 10, 135, 290, 70, 280, 5}));
        challengeButton.setPosition(465, 143);
        challengeButton.setTouchable(Touchable.enabled);
        stage.addActor(challengeButton);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(.3f, .7f, .8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        // 绘制主页内容
        game.batch.draw(AssetTexture.MAIN_SCREEN.get(assetMan), 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        game.batch.end();

        stage.getViewport().apply(); // 应用UI视口
        stage.act(delta); // 更新UI舞台（例如，执行Action）
        stage.draw();     // 绘制UI舞台


//        // 绘制多边形
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(1, 0, 0, 1); // 设置颜色为红色
//        shapeRenderer.polygon(polygon.getTransformedVertices());
//        shapeRenderer.end();
    }

    public void update(float delta) {
        elapsedTime += delta;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
        if (game.bgMusic != null) {
            game.bgMusic.stop();
        }
    }

    @Override
    public void dispose() {
        // 释放资源
        stage.dispose();
        if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
        shapeRenderer.dispose();
    }
}
