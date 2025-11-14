package com.example.plantsvszombies.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.example.plantsvszombies.PvzGame;
import com.example.plantsvszombies.Resource.AssetFont;
import com.example.plantsvszombies.Resource.AssetSound;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.ui.HomePageButton;
import com.example.plantsvszombies.ui.PvzDialogBox;
import com.example.plantsvszombies.ui.PvzGraveButton;

public class HomeScreen implements Screen {
    private final PvzGame game;

    private float elapsedTime = 0f;
    private final AssetMan assetMan;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final Stage stage;
//    private final BitmapFont dwarvenStoneCraft;


    private static final float WORLD_WIDTH = 900f;
    private static final float WORLD_HEIGHT = 600f;

    private final FitViewport viewport;
    private final OrthographicCamera camera;

    private Group modalOverlay;
    private Texture whitePixel;

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
        startAdventureButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });
        stage.addActor(startAdventureButton);

        HomePageButton miniGameButton = new HomePageButton(AssetTexture.MINI_GAME_BUTTON.get(assetMan),
            AssetTexture.MINI_GAME_HIGHLIGHT.get(assetMan),
            null, AssetSound.BLEEP.get(assetMan),
            new Polygon(new float[]{5, 65, 5, 135, 340, 90, 325, 5}));
        miniGameButton.setPosition(460, 295);
        miniGameButton.setTouchable(Touchable.enabled);
        miniGameButton.addListener(l -> {
            if (l instanceof InputEvent) {
                InputEvent event = (InputEvent) l;
                if (event.getType() == InputEvent.Type.touchDown) {
                    game.setScreen(new BowlingScreen(game));
                    return true;
                }
            }
            return false;
        });
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

        HomePageButton helpButton = new HomePageButton(AssetTexture.HELP_BUTTON.get(assetMan),
            AssetTexture.HELP_HIGHLIGHT.get(assetMan),
            null, AssetSound.BLEEP.get(assetMan),
            new Polygon(new float[]{-10, 5, -5, 30, 0, 40, 10, 55, 0, 70, 50, 70, 40, 50, 60, 5, 25, -10}));
        helpButton.setPosition(732, 53);
        helpButton.setTouchable(Touchable.enabled);
        stage.addActor(helpButton);

        HomePageButton optionsButton = new HomePageButton(AssetTexture.OPTIONS_BUTTON.get(assetMan),
            AssetTexture.OPTIONS_HIGHLIGHT.get(assetMan),
            null, AssetSound.BLEEP.get(assetMan),
            new Polygon(new float[]{-10, 15, 5, 35, 30, 50, 35, 75, 60, 75, 60, 50, 90, 15, 80, 0, 35, -10}));
        optionsButton.setPosition(640, 80);
        optionsButton.setTouchable(Touchable.enabled);
        stage.addActor(optionsButton);

        HomePageButton quitButton = new HomePageButton(AssetTexture.QUIT_BUTTON.get(assetMan),
            AssetTexture.QUIT_HIGHLIGHT.get(assetMan),
            null, AssetSound.BLEEP.get(assetMan),
            new Polygon(new float[]{-20, 15, -20, 40, -5, 55, 55, 55, 65, 20, 55, 0, 10, -10}));
        quitButton.setPosition(815, 58);
        quitButton.setTouchable(Touchable.enabled);
        quitButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                BitmapFont dwarvenStoneCraft = AssetFont.DWARVEN_STONECRAFT_SIZE30_DEFAULT.get(assetMan);

                PvzGraveButton quitButton = new PvzGraveButton("Quit", dwarvenStoneCraft);
                PvzGraveButton cancelButton = new PvzGraveButton("Cancel", dwarvenStoneCraft);
                PvzDialogBox dialogBox = new PvzDialogBox("Quit", "Are you sure you wish to quit the game?",
                    new PvzGraveButton[]{quitButton, cancelButton});

                quitButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Gdx.app.exit();
                    }
                });
                cancelButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        hideModal();
                    }
                });
                dialogBox.setPosition( (WORLD_WIDTH - dialogBox.getWidth()) / 2f, (WORLD_HEIGHT - dialogBox.getHeight()) / 2f);
                showModal(dialogBox, 0f);
                return true;
            }
        });
        stage.addActor(quitButton);

        HomePageButton almanac = new HomePageButton(AssetTexture.ALMANAC.get(assetMan),
            AssetTexture.ALMANAC_HIGHLIGHT.get(assetMan),
            null, AssetSound.BLEEP.get(assetMan),
            new Polygon(new float[]{5, 25, 25, 95, 90, 80, 75, 30, 75, 5}));
        almanac.setPosition(370, 60);
        almanac.setTouchable(Touchable.enabled);
        almanac.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new AlmanacScreen(game));
            }
        });
        stage.addActor(almanac);
    }

    private void ensureWhitePixel() {
        if (whitePixel == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(1f, 1f, 1f, 1f);
            pixmap.fill();
            whitePixel = new Texture(pixmap);
            pixmap.dispose();
        }
    }

    // 显示模态对话框
    private void showModal(Actor dialog, float overlayAlpha) {
        ensureWhitePixel();
        if (modalOverlay != null) modalOverlay.remove();

        modalOverlay = new Group();
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        modalOverlay.setSize(w, h);
        modalOverlay.setTouchable(Touchable.enabled);

        Image bg = new Image(whitePixel);
        bg.setSize(w, h);
        bg.setColor(0f, 0f, 0f, overlayAlpha);
        bg.setTouchable(Touchable.enabled);

        bg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Vector2 stageCoords = modalOverlay.localToStageCoordinates(new Vector2(x, y));
                Vector2 dialogLocal = dialog.stageToLocalCoordinates(stageCoords);
                com.badlogic.gdx.scenes.scene2d.Actor hit = dialog.hit(dialogLocal.x, dialogLocal.y, true);
                // 如果点在 dialog 内，返回 false 让事件继续传递给 dialog；否则消费事件
                return hit == null;
            }
        });

        modalOverlay.addActor(bg);

        // 确保 dialog 尺寸已计算（如果是 Table/Widget 可调用 validate/pref size）
        dialog.setPosition((w - dialog.getWidth()) / 2f, (h - dialog.getHeight()) / 2f);
        modalOverlay.addActor(dialog);

        stage.addActor(modalOverlay);
    }

    // 隐藏模态对话框
    private void hideModal() {
        if (modalOverlay != null) {
            modalOverlay.remove();
            modalOverlay = null;
        }
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

        if (whitePixel != null) {
            whitePixel.dispose();
            whitePixel = null;
        }
    }
}
