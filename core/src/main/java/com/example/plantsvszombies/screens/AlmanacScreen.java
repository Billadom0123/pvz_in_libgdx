package com.example.plantsvszombies.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.example.plantsvszombies.PvzGame;
import com.example.plantsvszombies.Resource.*;
import com.example.plantsvszombies.Resource.Almanac.AlmanacPlant;
import com.example.plantsvszombies.Resource.Almanac.AlmanacZombie;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.managers.SoundManager;
import com.example.plantsvszombies.models.Plants.BasePlant;
import com.example.plantsvszombies.models.Plants.Sunflower;
import com.example.plantsvszombies.models.Zombies.BaseZombie;
import com.example.plantsvszombies.models.Zombies.NormalZombie;
import com.example.plantsvszombies.ui.*;

public class AlmanacScreen implements Screen {

    private enum State {
        INDEX,
        PLANT_DETAILS,
        ZOMBIE_DETAILS
    }
    private State state;
    private final PvzGame game;
    private final SoundManager soundManager;
    private final Screen previousScreen;

    private float elapsedTime = 0f;
    private final AssetMan assetMan;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final Skin uiSkin;
    private final Skin pvzCustomUiSkin;
    private final Stage stage;


    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 600f;

    private final FitViewport viewport;
    private final OrthographicCamera camera;

    private Group modalOverlay;
    private Texture whitePixel;

    // Index页
    private Label indexTitleLabel;
    private Sunflower indexSunflower;
    private NormalZombie indexZombie;
    private PressableGroup plantDetailBtn;
    private PressableGroup zombieDetailBtn;
    private PressableGroup indexBtn;
    private PressableGroup closeBtn;

    private BitmapFont detailFont;
    // 植物详情页
    private boolean isPlantDetailsInitialized = false;
    private Label plantTitleLabel;
    private final Array<AlmanacPlantCard> plantCards;
    private Texture plantCardBackground;
    private AlmanacPlantCard selectedPlantCard;
    private BasePlant selectedPlant;
    private Label plantNameLabel;
    private Table plantDetailTable;
    private Label plantCostLabel;
    private Label rechargeLabel;

    // 僵尸详情页
    private boolean isZombieDetailsInitialized = false;
    private Label zombieTitleLabel;
    private Array<AlmanacZombieCard> zombieCards;
    private AlmanacZombieCard selectedZombieCard;
    private BaseZombie selectedZombie;
    private Label zombieNameLabel;
    private Table zombieDetailTable;

    public AlmanacScreen(PvzGame game, Screen previous) {
        this.state = State.INDEX;
        this.game = game;
        this.previousScreen = previous;
        this.assetMan = game.assetManager;
        this.soundManager = game.soundManager;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        camera.position.set(WORLD_HEIGHT / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        plantCards = new Array<>();
        zombieCards = new Array<>();

        uiSkin = new Skin(Gdx.files.internal("graphics/Skin/uiskin.json"));
        pvzCustomUiSkin = AssetSkin.PVZ_CUSTOM_SKIN.get(assetMan);

        detailFont = AssetFont.BRIANNE_TOD_SIZE15_DEFAULT.get(assetMan);
        detailFont.getData().markupEnabled = true;
        detailFont.setUseIntegerPositions(false);
        detailFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Linear);
        detailFont.getData().setLineHeight(detailFont.getLineHeight() - 1f);

        stage = new Stage(viewport, game.batch);
        initIndexPage();
    }

    private void initIndexPage() {
        indexTitleLabel = new Label("Suburban Almanac - Index", pvzCustomUiSkin.get("HouseofTerror28", Label.LabelStyle.class));
        indexTitleLabel.setWidth(WORLD_WIDTH);
        indexTitleLabel.setAlignment(Align.center);
        indexTitleLabel.setPosition(0, 525);
        stage.addActor(indexTitleLabel);

        indexSunflower = new Sunflower(null, 175, 290);
        indexZombie = new NormalZombie(null, 500, 270);

        BitmapFont font = AssetFont.DWARVEN_STONECRAFT_SIZE24_DEFAULT.get(assetMan);

        plantDetailBtn = new PressableGroup();
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

        plantDetailBtn.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                plantBtnGlow.setVisible(true);
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                plantBtnGlow.setVisible(false);
            }
        });
        plantDetailBtn.setClickListener(() -> {
            soundManager.playSound(AssetSound.TAP1);
            state = State.PLANT_DETAILS;
            if (!isPlantDetailsInitialized) {
                isPlantDetailsInitialized = true;
                initPlantDetailsPage();
            }

            stage.clear();
            stage.addActor(plantTitleLabel);
            for (AlmanacPlantCard card : plantCards) stage.addActor(card);
            stage.addActor(plantNameLabel);
            stage.addActor(plantDetailTable);
            stage.addActor(plantCostLabel);
            stage.addActor(rechargeLabel);
            stage.addActor(indexBtn);
            stage.addActor(closeBtn);
        });
        plantDetailBtn.setPosition(135, 205);
        stage.addActor(plantDetailBtn);

        zombieDetailBtn = new PvzGraveButton("View Zombies");
        zombieDetailBtn.setClickListener(() -> {
            soundManager.playSound(AssetSound.TAP1);
            state = State.ZOMBIE_DETAILS;
            if (!isZombieDetailsInitialized) {
                isZombieDetailsInitialized = true;
                initZombieDetails();
            }

            stage.clear();
            stage.addActor(zombieTitleLabel);
            for (AlmanacZombieCard zombieCard : zombieCards) stage.addActor(zombieCard);
            stage.addActor(zombieNameLabel);
            stage.addActor(zombieDetailTable);
            stage.addActor(indexBtn);
            stage.addActor(closeBtn);
        });
        zombieDetailBtn.setPosition(500, 205);
        stage.addActor(zombieDetailBtn);

        indexBtn = new PressableGroup();
        TextureActor indexBtnBg = new TextureActor(AssetTexture.ALMANAC_INDEX_BUTTON.get(assetMan));
        indexBtnBg.setPosition(0, 0);
        TextureActor indexBtnGlow = new TextureActor(AssetTexture.ALMANAC_INDEX_BUTTON_HIGHLIGHT.get(assetMan));
        indexBtnGlow.setPosition(0, 0);
        indexBtnGlow.setVisible(false);
        BitmapFont font14 = AssetFont.BRIANNE_TOD_SIZE15_DEFAULT.get(assetMan);
        Label indexBtnLabel = new Label("ALMANAC INDEX", new Label.LabelStyle(font14, new Color(42/255f, 49/255f, 91/255f, 1f)));
        indexBtnLabel.pack();
        indexBtnLabel.setPosition((indexBtnBg.getWidth() - indexBtnLabel.getWidth()) / 2f,
                (indexBtnBg.getHeight() - indexBtnLabel.getHeight()) / 2f);
        indexBtn.setSize(indexBtnBg.getWidth(), indexBtnBg.getHeight());
        indexBtn.addActor(indexBtnBg);
        indexBtn.addActor(indexBtnGlow);
        indexBtn.addActor(indexBtnLabel);
        indexBtn.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                indexBtnGlow.setVisible(true);
                indexBtnBg.setVisible(false);
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                indexBtnGlow.setVisible(false);
                indexBtnBg.setVisible(true);
            }
        });
        indexBtn.setClickListener(() -> {
            soundManager.playSound(AssetSound.TAP1);
            state = State.INDEX;

            stage.clear();
            stage.addActor(indexTitleLabel);
            stage.addActor(plantDetailBtn);
            stage.addActor(zombieDetailBtn);
            stage.addActor(closeBtn);
        });

        indexBtn.setPosition(15, 7);

        closeBtn = new PressableGroup();
        TextureActor closeBtnBg = new TextureActor(AssetTexture.ALMANAC_CLOSE_BUTTON.get(assetMan));
        closeBtnBg.setPosition(0, 0);
        TextureActor closeBtnHighlight = new TextureActor(AssetTexture.ALMANAC_CLOSE_BUTTON_HIGHLIGHT.get(assetMan));
        closeBtnHighlight.setPosition(0, 0);
        closeBtnHighlight.setVisible(false);
        Label closeBtnLabel = new Label("CLOSE", new Label.LabelStyle(font14, new Color(42/255f, 49/255f, 91/255f, 1f)));
        closeBtnLabel.pack();
        closeBtnLabel.setPosition((closeBtnBg.getWidth() - closeBtnLabel.getWidth()) / 2f - 5f, // 微调X位置5f
                (closeBtnBg.getHeight() - closeBtnLabel.getHeight()) / 2f);
        closeBtn.setSize(closeBtnBg.getWidth(), closeBtnBg.getHeight());
        closeBtn.addActor(closeBtnBg);
        closeBtn.addActor(closeBtnHighlight);
        closeBtn.addActor(closeBtnLabel);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                closeBtnHighlight.setVisible(true);
                closeBtnBg.setVisible(false);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                closeBtnHighlight.setVisible(false);
                closeBtnBg.setVisible(true);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                soundManager.playSound(AssetSound.TAP1);
                game.setScreen(previousScreen);
            }
        });
        closeBtn.setPosition(690, 7);
        stage.addActor(closeBtn);
    }

    private void initPlantDetailsPage() {

        plantTitleLabel = new Label("Suburban Almanac - Plants", pvzCustomUiSkin.get("HouseofTerrorYellow20", Label.LabelStyle.class));
        plantTitleLabel.setWidth(WORLD_WIDTH);
        plantTitleLabel.setAlignment(Align.center);
        plantTitleLabel.setPosition(0, 537);

        plantNameLabel = new Label("Plant", pvzCustomUiSkin.get("Yellow18", Label.LabelStyle.class));
        plantNameLabel.setPosition(575, 305);
        plantNameLabel.setWidth(100f);
        plantNameLabel.setAlignment(Align.center);

        plantDetailTable = new Table();
        plantDetailTable.setPosition(490, 295);
        plantDetailTable.setWidth(260f);
        plantDetailTable.top();

        plantCostLabel = new Label("Cost: ", new Label.LabelStyle(detailFont, Color.WHITE));
        plantCostLabel.setPosition(490, 60);

        rechargeLabel = new Label("Recharge: ", new Label.LabelStyle(detailFont, Color.WHITE));
        rechargeLabel.setPosition(680, 60);

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

    private void initZombieDetails() {

        zombieTitleLabel = new Label("Suburban Almanac - Zombies", pvzCustomUiSkin.get("DwarvenGreen24", Label.LabelStyle.class));
        zombieTitleLabel.setWidth(WORLD_WIDTH);
        zombieTitleLabel.setAlignment(Align.center);
        zombieTitleLabel.setPosition(0, 535);


        zombieNameLabel = new Label("Zombie", pvzCustomUiSkin.get("Green18", Label.LabelStyle.class));
        zombieNameLabel.setPosition(580, 235);
        zombieNameLabel.setWidth(100f);
        zombieNameLabel.setAlignment(Align.center);

        zombieDetailTable = new Table();
        zombieDetailTable.setPosition(495, 230);
        zombieDetailTable.setWidth(260f);
        zombieDetailTable.top();


        int gapX = 85; // 卡片之间的水平间距
        int startX = 20; // 起始X位置
        int startY = 438; // 起始Y位置

        for (AlmanacZombie zombie : AlmanacZombie.values()) {
            AlmanacZombieCard zombieCard = new AlmanacZombieCard(zombie);
            zombieCard.setPosition(startX, startY);
            zombieCard.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showZombieDetails(zombieCard);
                }
            });
            zombieCards.add(zombieCard);
            stage.addActor(zombieCard);
            startX += gapX;
        }
        showZombieDetails(zombieCards.get(0));
    }

    private void showPlantDetails(AlmanacPlantCard card) {
        if (selectedPlantCard == card) return; // 已经是选中的卡片，直接返回
        soundManager.playSound(AssetSound.TAP1);
        selectedPlantCard = card;
        plantCardBackground = card.getAlmanacPlant().getBackground().get(assetMan);
        selectedPlant = BasePlant.create(card.getAlmanacPlant().getPlantClass(), null, 590, 380);
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
            plantDetailTable.add(descLabel).width(plantDetailTable.getWidth()).padBottom(4f).row();
            JsonValue tips = value.get("tips");
            for (JsonValue field = tips.child; field != null; field = field.next) {
                Label tipLabel = new Label("[#a52a2a]" + field.name + ":[] [#FF0000]" + field.asString() + "[]",
                        new Label.LabelStyle(detailFont, Color.WHITE));
                tipLabel.setWrap(true);
                tipLabel.setWidth(plantDetailTable.getWidth());
                plantDetailTable.add(tipLabel).width(plantDetailTable.getWidth()).padBottom(-1f).row();
            }
            Label storyLabel = new Label(value.getString("story"),
                    new Label.LabelStyle(detailFont, new Color(165/255f, 42/255f, 42/255f, 1f)));
            storyLabel.setWrap(true);
            storyLabel.setWidth(plantDetailTable.getWidth());
            plantDetailTable.add(storyLabel).width(plantDetailTable.getWidth()).padTop(4f).row();

            plantCostLabel.setText("[#a52a2a]Cost:[] [#FF0000]" + value.getString("cost") + "[]");

            rechargeLabel.setText("[#a52a2a]Recharge:[] [#FF0000]" + value.getString("recharge") + "[]");
            rechargeLabel.pack();
            rechargeLabel.setPosition(750 - rechargeLabel.getWidth(), 60);
        }
    }

    private void showZombieDetails(AlmanacZombieCard card) {
        if (selectedZombieCard == card) return;
        soundManager.playSound(AssetSound.TAP1);
        selectedZombieCard = card;
        selectedZombie = BaseZombie.create(card.getZombieClass(), null, 590, 380);
        selectedZombie.setPositionByCenter(640, 380);
        String jsonFilePath = card.getAlmanacZombie().getJsonFilePath();
        if (jsonFilePath != null && !jsonFilePath.isEmpty()) {
            String jsonContent = Gdx.files.internal(jsonFilePath).readString();
            JsonReader reader = new JsonReader();
            JsonValue value = reader.parse(jsonContent);
            zombieNameLabel.setText(value.getString("name"));
            zombieDetailTable.clear();
            Label descLabel = new Label(value.getString("description"),
                new Label.LabelStyle(detailFont, new Color(40/255f, 50/255f, 90/255f, 1f)));
            descLabel.setWrap(true);
            descLabel.setWidth(zombieDetailTable.getWidth());
            zombieDetailTable.add(descLabel).width(zombieDetailTable.getWidth()).padBottom(4f).row();
            JsonValue tips = value.get("tips");
            for (JsonValue field = tips.child; field != null; field = field.next) {
                Label tipLabel = new Label("[#8f431b]" + field.name + ":[] [#cc241d]" + field.asString() + "[]",
                    new Label.LabelStyle(detailFont, Color.WHITE));
                tipLabel.setWrap(true);
                tipLabel.setWidth(zombieDetailTable.getWidth());
                zombieDetailTable.add(tipLabel).width(zombieDetailTable.getWidth()).padBottom(-1f).row();
            }
            Label storyLabel = new Label(value.getString("story"),
                new Label.LabelStyle(detailFont, new Color(143/255f, 67/255f, 27/255f, 1f)));
            storyLabel.setWrap(true);
            storyLabel.setWidth(zombieDetailTable.getWidth());
            zombieDetailTable.add(storyLabel).width(zombieDetailTable.getWidth()).padTop(4f).row();
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

//            Batch batch = stage.getBatch();
//            batch.setProjectionMatrix(stage.getCamera().combined);
//
//            batch.begin();
//            plantDetailBtn.draw(batch, 1f);
//            zombieDetailBtn.draw(batch, 1f);
//            closeBtn.draw(batch, 1f);
//            batch.end();


        } else if (state == State.PLANT_DETAILS) {
            // 绘制植物详情页内容
            game.batch.begin();
            game.batch.draw(AssetTexture.ALMANAC_PLANT_BACK.get(assetMan), 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            game.batch.draw(plantCardBackground, 525, 300);
            game.batch.draw(AssetTexture.ALMANAC_PLANT_CARD.get(assetMan), 465, 30);
            selectedPlant.drawShadow(game.batch);
            selectedPlant.drawAnimation(game.batch);
            game.batch.end();

//            Batch batch = stage.getBatch();
//            batch.setProjectionMatrix(stage.getCamera().combined);
//
//            batch.begin();
//            for (AlmanacPlantCard card : plantCards) card.draw(batch, 1f);
//            plantNameLabel.draw(batch, 1f);
//            plantDetailTable.draw(batch, 1f);
//            plantCostLabel.draw(batch, 1f);
//            rechargeLabel.draw(batch, 1f);
//            indexBtn.draw(batch, 1f);
//            closeBtn.draw(batch, 1f);
//            batch.end();


        } else if (state == State.ZOMBIE_DETAILS) {
            // 绘制僵尸详情页内容
            game.batch.begin();
            game.batch.draw(AssetTexture.ALMANAC_ZOMBIE_BACK.get(assetMan), 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            game.batch.draw(AssetTexture.ALMANAC_GROUND_DAY.get(assetMan), 525, 280);
            game.batch.draw(AssetTexture.ALMANAC_ZOMBIE_CARD.get(assetMan), 465, 30);
            selectedZombie.drawShadow(game.batch);
            selectedZombie.drawAnimation(game.batch);
            game.batch.end();
//
//            Batch batch = stage.getBatch();
//            batch.setProjectionMatrix(stage.getCamera().combined);

//            batch.begin();
//            AlmanacZombieCard nor = new AlmanacZombieCard(AlmanacZombie.NORMAL_ZOMBIE);
//            nor.setPosition(200, 300);
//            nor.draw(batch, 1f);
//
//            indexBtn.draw(batch, 1f);
//            closeBtn.draw(batch, 1f);
//            batch.end();
        }

        stage.draw();
    }

    public void update(float delta) {
        elapsedTime += delta;
        indexSunflower.stillUpdate(delta);
        indexZombie.stillUpdate(delta);
        if (selectedPlantCard != null) selectedPlant.stillUpdate(delta);
        if (selectedZombieCard != null) selectedZombie.stillUpdate(delta);
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
                Actor hit = dialog.hit(dialogLocal.x, dialogLocal.y, true);
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
        SoundManager.getInstance().changeMusic(AssetMusic.CHOOSE_YOUR_SEEDS, true);
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
