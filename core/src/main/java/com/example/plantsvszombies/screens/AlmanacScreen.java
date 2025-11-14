package com.example.plantsvszombies.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.example.plantsvszombies.PvzGame;
import com.example.plantsvszombies.Resource.Almanac.AlmanacPlant;
import com.example.plantsvszombies.Resource.AssetFont;
import com.example.plantsvszombies.Resource.AssetMusic;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.models.Plants.BasePlant;
import com.example.plantsvszombies.models.Plants.Sunflower;
import com.example.plantsvszombies.models.Zombies.NormalZombie;
import com.example.plantsvszombies.ui.AlmanacPlantCard;
import com.example.plantsvszombies.ui.PvzGraveButton;
import com.example.plantsvszombies.ui.TextureActor;

public class AlmanacScreen implements Screen {

    private enum State {
        INDEX,
        PLANT_DETAILS,
        ZOMBIE_DETAILS
    }
    private State state;
    private final PvzGame game;

    private float elapsedTime = 0f;
    private final AssetMan assetMan;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final Skin uiSkin;
    private final Stage stage;


    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 600f;

    private final FitViewport viewport;
    private final OrthographicCamera camera;

    private Group modalOverlay;
    private Texture whitePixel;

    // Index页
    private Sunflower indexSunflower;
    private NormalZombie indexZombie;
    private Group plantDetailBtn;
    private Group zombieDetailBtn;
    private Group indexBtn;

    // 植物详情页
    private Array<AlmanacPlantCard> plantCards;
    private Texture cardBackground;
    private AlmanacPlantCard selectedPlantCard;
    private BasePlant selectedPlant;
    private Label plantNameLabel;
    private BitmapFont detailFont;
    private Table plantDetailTable;
    private Label plantCostLabel;
    private Label rechargeLabel;

    public AlmanacScreen(PvzGame game) {
        this.state = State.INDEX;
        this.game = game;
        this.assetMan = game.assetManager;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        camera.position.set(WORLD_HEIGHT / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        plantCards = new Array<>();

        uiSkin = new Skin(Gdx.files.internal("graphics/Skin/uiskin.json"));
        stage = new Stage(viewport, game.batch);
        initIndexPage();
    }

    private void initIndexPage() {
        indexSunflower = new Sunflower(null, 175, 290);
        indexZombie = new NormalZombie(null, 500, 270);

        BitmapFont font = AssetFont.DWARVEN_STONECRAFT_SIZE24_DEFAULT.get(assetMan);

        plantDetailBtn = new Group();
        TextureActor plantBtnBg = new TextureActor(AssetTexture.SEED_CHOOSER_BUTTON.get(assetMan));
        plantBtnBg.setPosition(0, 0);
        TextureActor plantBtnGlow = new TextureActor(AssetTexture.SEED_CHOOSER_BUTTON_GLOW.get(assetMan));
        plantBtnGlow.setPosition(0, 0);
        plantBtnGlow.setVisible(false);
        Label plantBtnLabel = new Label("View Plants", new Label.LabelStyle(font, new Color(213/255f, 159/255f, 43/255f, 1)));
        plantBtnLabel.pack();
        plantBtnLabel.setPosition((plantBtnBg.getWidth() - plantBtnLabel.getWidth()) / 2f,
                (plantBtnBg.getHeight() - plantBtnLabel.getHeight()) / 2f);
        plantDetailBtn.setSize(plantBtnBg.getWidth(), plantBtnBg.getHeight());
        plantDetailBtn.addActor(plantBtnBg);
        plantDetailBtn.addActor(plantBtnGlow);
        plantDetailBtn.addActor(plantBtnLabel);

        plantDetailBtn.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                plantBtnGlow.setVisible(true);
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                plantBtnGlow.setVisible(false);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                state = State.PLANT_DETAILS;
                initPlantDetailsPage();
            }
        });
        plantDetailBtn.setPosition(135, 205);
        stage.addActor(plantDetailBtn);

        zombieDetailBtn = new PvzGraveButton("View Zombies", font);
        zombieDetailBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 切换到僵尸详情页的逻辑
            }
        });
        zombieDetailBtn.setPosition(500, 205);
        stage.addActor(zombieDetailBtn);
    }

    private void initPlantDetailsPage() {
        // 初始化植物详情页内容
        BitmapFont font = AssetFont.DWARVEN_STONECRAFT_SIZE30_DEFAULT.get(assetMan);
        detailFont = AssetFont.BRIANNE_S_HAND_SIZE14_DEFAULT.get(assetMan);
        detailFont.getData().markupEnabled = true;

        plantNameLabel = new Label("", new Label.LabelStyle(font, new Color(213/255f, 159/255f, 43/255f, 1)));
        plantNameLabel.setPosition(575, 315);
        plantNameLabel.setWidth(100f);
        plantNameLabel.setAlignment(Align.center);
        stage.addActor(plantNameLabel);

        plantDetailTable = new Table();
        plantDetailTable.setPosition(490, 295);
        plantDetailTable.setWidth(260f);
        plantDetailTable.top();
        stage.addActor(plantDetailTable);

        plantCostLabel = new Label("Cost: ", new Label.LabelStyle(detailFont, Color.WHITE));
        plantCostLabel.setPosition(490, 60);
        stage.addActor(plantCostLabel);

        rechargeLabel = new Label("Recharge: ", new Label.LabelStyle(detailFont, Color.WHITE));
        rechargeLabel.setPosition(680, 60);
        stage.addActor(rechargeLabel);

        int gapX = 52; // 卡片之间的水平间距
        int startX = 26; // 起始X位置
        int startY = 438; // 起始Y位置

        for (AlmanacPlant plant : AlmanacPlant.values()) {
            AlmanacPlantCard card = new AlmanacPlantCard(plant, uiSkin);
            card.setPosition(startX, startY);
            card.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showPlantDetails(card);
                }
            });
            plantCards.add(card);
            stage.addActor(card);
            startX += gapX;
        }
        showPlantDetails(plantCards.get(0));
    }

    private void showPlantDetails(AlmanacPlantCard card) {
        if (selectedPlantCard == card) return; // 已经是选中的卡片，直接返回
        selectedPlantCard = card;
        cardBackground = card.getAlmanacPlant().getBackground().get(assetMan);
        selectedPlant = BasePlant.create(card.getPlantClass(), null, 590, 380);
        String jsonFilePath = card.getAlmanacPlant().getJsonFilePath();
        if (jsonFilePath != null && !jsonFilePath.isEmpty()) {
            String jsonContent = Gdx.files.internal(jsonFilePath).readString();
            JsonReader reader = new JsonReader();
            JsonValue value = reader.parse(jsonContent);
            plantNameLabel.setText(value.getString("name"));
            plantDetailTable.clear();
            Label descLabel = new Label(value.getString("description"),
                    new Label.LabelStyle(detailFont, new Color(0f, 0f, 139/255f, 1f)));
            descLabel.setWrap(true);
            descLabel.setWidth(plantDetailTable.getWidth());
            plantDetailTable.add(descLabel).width(plantDetailTable.getWidth()).padBottom(10f).row();
            JsonValue tips = value.get("tips");
            for (JsonValue field = tips.child; field != null; field = field.next) {
                Label tipLabel = new Label("[#a52a2a]" + field.name + ":[] [#FF0000]" + field.asString() + "[]",
                        new Label.LabelStyle(detailFont, Color.WHITE));
                tipLabel.setWrap(true);
                tipLabel.setWidth(plantDetailTable.getWidth());
                plantDetailTable.add(tipLabel).width(plantDetailTable.getWidth()).padBottom(2f).row();
            }
            Label storyLabel = new Label(value.getString("story"),
                    new Label.LabelStyle(detailFont, new Color(165/255f, 42/255f, 42/255f, 1f)));
            storyLabel.setWrap(true);
            storyLabel.setWidth(plantDetailTable.getWidth());
            plantDetailTable.add(storyLabel).width(plantDetailTable.getWidth()).padTop(10f).row();

            plantCostLabel.setText("[#a52a2a]Cost:[] [#FF0000]" + value.getString("cost") + "[]");

            rechargeLabel.setText("[#a52a2a]Recharge:[] [#FF0000]" + value.getString("recharge") + "[]");
            rechargeLabel.pack();
            rechargeLabel.setPosition(750 - rechargeLabel.getWidth(), 60);
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(.3f, .7f, .8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        stage.getViewport().apply(); // 应用UI视口
        stage.act(delta); // 更新UI舞台（例如，执行Action）

        if (state == State.INDEX) {
            game.batch.begin();
            // 绘制主页内容
            game.batch.draw(AssetTexture.ALMANAC_INDEX_BACKGROUND.get(assetMan), 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            indexSunflower.drawShadow(game.batch);
            indexSunflower.drawAnimation(game.batch);
            indexZombie.drawShadow(game.batch);
            indexZombie.drawAnimation(game.batch);
            game.batch.end();

            Batch batch = stage.getBatch();
            batch.setProjectionMatrix(stage.getCamera().combined);

            batch.begin();
            plantDetailBtn.draw(batch, 1f);
            zombieDetailBtn.draw(batch, 1f);
            batch.end();
        } else if (state == State.PLANT_DETAILS) {
            // 绘制植物详情页内容
            game.batch.begin();
            game.batch.draw(AssetTexture.ALMANAC_PLANT_BACK.get(assetMan), 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            game.batch.draw(cardBackground, 525, 300);
            game.batch.draw(AssetTexture.ALMANAC_PLANT_CARD.get(assetMan), 465, 30);
            selectedPlant.drawShadow(game.batch);
            selectedPlant.drawAnimation(game.batch);
            game.batch.end();

            Batch batch = stage.getBatch();
            batch.setProjectionMatrix(stage.getCamera().combined);

            batch.begin();
            for (AlmanacPlantCard card : plantCards) card.draw(batch, 1f);
            plantNameLabel.draw(batch, 1f);
            plantDetailTable.draw(batch, 1f);
            plantCostLabel.draw(batch, 1f);
            rechargeLabel.draw(batch, 1f);
            batch.end();
        }

    }

    public void update(float delta) {
        elapsedTime += delta;
        indexSunflower.stillUpdate(delta);
        indexZombie.stillUpdate(delta);
        if (selectedPlantCard != null) selectedPlant.stillUpdate(delta);
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
        if (game.bgMusic != AssetMusic.CHOOSE_YOUR_SEEDS.get(assetMan)) {
            if (game.bgMusic != null) game.bgMusic.stop();
            game.bgMusic = AssetMusic.CHOOSE_YOUR_SEEDS.get(assetMan);
            game.bgMusic.setLooping(true);
            game.bgMusic.setVolume(0.5f);
            game.bgMusic.play();
        }
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
