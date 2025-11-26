package com.example.plantsvszombies.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.example.plantsvszombies.PvzGame;
import com.example.plantsvszombies.Resource.*;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.managers.GameManager;
import com.example.plantsvszombies.managers.SoundManager;
import com.example.plantsvszombies.models.Crazy.DancingSun;
import com.example.plantsvszombies.models.DamageType;
import com.example.plantsvszombies.models.GameDialog;
import com.example.plantsvszombies.models.LawnMower;
import com.example.plantsvszombies.models.Plants.*;
import com.example.plantsvszombies.models.Projectiles.BaseProjectile;
import com.example.plantsvszombies.models.Sun;
import com.example.plantsvszombies.models.Texture.Falling;
import com.example.plantsvszombies.models.Util.ConditionalExecutor;
import com.example.plantsvszombies.models.Zombies.*;
import com.example.plantsvszombies.ui.*;

import java.util.*;

public class GameScreen implements BaseGameScreen {

    // --- 核心引用 ---
    private final PvzGame game; // 对主游戏类的引用，用于访问全局对象如SpriteBatch
    private final AssetMan assetManager;
    private final SoundManager soundManager;
    private final GameManager gameManager;

    // --- 渲染相关 ---
    private final OrthographicCamera gameCamera; // 游戏世界的相机
    private final FitViewport gameViewport;     // 保持游戏世界比例的视口
    private final ShapeRenderer shapeRenderer;
    public static final float WORLD_WIDTH = 1400;  // 游戏世界的宽度
    public static final float WORLD_HEIGHT = 600; // 游戏世界的高度

    // 相机相关（坐标缓存为字段，避免在 resize 时丢失）
    private final float cameraMinX;
    private final float cameraMaxX;
    private final float cameraMinY;
    private final float cameraMaxY;

    private boolean isCameraMoving = false; // 标记相机是否正在移动
    private float cameraTargetX;
    private float cameraLerpSpeed = 50f; // 控制移动速度
    private float cameraMoveDuration = 0f;    // 平滑移动总时长（秒），<=0 表示未使用平滑模式
    private float cameraMoveElapsed = 0f;     // 已经过的时间
    private float cameraStartX = 0f;          // 平滑移动起始 X
    private boolean cameraSmoothMode = false; // 是否使用平滑时长模式

    private float cameraShakeTime = 0f;
    private float cameraShakeDuration = 0f;
    private float cameraShakeMagnitude = 0f;
    private final Vector2 cameraShakeOrigin = new Vector2();

    private final float cellWidth = 81;   // 这些数值由背景图测量得到
    private final float cellHeight = 98;
    private final float startX = 255;
    private final float startY = 35;

    private int highlightedRow = -1; // 当前高亮显示的行，-1表示没有高亮
    private int highlightedCol = -1; // 当前高亮显示的列，-1表示没有高亮

    // --- 游戏对象列表 ---
    private final Array<BasePlant> plants;
    private final Array<BaseZombie> zombies;
    private final Array<BaseProjectile> projectiles;
    private final Array<Sun> suns;
    private final Array<LawnMower> lawnMowers;
    private final Array<Falling> fallings;
    private final Array<ParticleEffect> particles;
    private final Array<PlantCard> plantCards; // 植物卡片列表，仅作获取用途
    private final Array<GameDialog> gameDialogs;

    // --- 游戏逻辑 ---
    private float gameTime = 0f;          // 游戏进行的总时间
    private final Rectangle[][] gridCells; // 逻辑网格，用于定位和种植
    private float zombieSpawnTimer;      // 僵尸生成计时器
    private float ZOMBIE_SPAWN_INTERVAL = 8.0f; // 默认僵尸生成间隔
    private float sunSpawnTimer;         // 太阳生成计时器
    private float SUN_SPAWN_INTERVAL = 12.0f; // 太阳生成间隔
    private boolean firstZombieSpawned = false;
    private boolean firstWaveSpawned = false;
    private ConditionalExecutor conditionalExecutor;


    // --- UI 相关 ---
    private final Skin uiSkin;
    private final Stage uiStage;      // UI专属的舞台
    private Table topBar;
    private final Label sunLabel;     // 显示太阳数量的标签
    private PlantCard selectedCard; // 当前被玩家选中的植物卡片
    private Table tooltipTable;    // 显示卡片信息的提示框
    private Shovel shovel;          // 铲子UI元素
    private PvzGraveButton pauseButton; // 暂停按钮
    private Texture whitePixel;

    private final Array<Group> modalStack = new Array<>(); // 替换单一模态层为栈

    private float endBlackOverlayAlpha = 0f; // 黑色覆盖层的透明度
    private float endWordScale = 0f; // 抖动计时器

    /**
     * GameScreen的构造函数
     */
    public GameScreen(PvzGame game) {
        this.game = game;
        this.assetManager = AssetMan.getInstance();
        this.soundManager = SoundManager.getInstance();
        this.gameManager = GameManager.getInstance();

        // 1. 初始化游戏相机和视口
        gameCamera = new OrthographicCamera();
        float minX = startX - cellWidth + 30;
        float minY = 0;
        // 缓存范围到字段
        this.cameraMinX = minX;
        this.cameraMaxX = minX + 800f;
        this.cameraMinY = minY;
        this.cameraMaxY = minY + 600f;

        gameViewport = new FitViewport(800, 600, gameCamera);
        gameCamera.position.set(400, 300, 0); // 相机居中到可视范围
        gameCamera.update();
        shapeRenderer = new ShapeRenderer();

        // 2. 初始化游戏对象列表
        plants = new Array<>();
        zombies = new Array<>();
        projectiles = new Array<>();
        suns = new Array<>();
        lawnMowers = new Array<>();
        fallings = new Array<>();
        particles = new Array<>();
        plantCards = new Array<>();
        gameDialogs = new Array<>();
        conditionalExecutor = new ConditionalExecutor();

        // 3. 初始化UI（与游戏共用同一 viewport，避免相机裁切后的偏移）
        uiSkin = new Skin(Gdx.files.internal("graphics/Skin/uiskin.json"));
        uiStage = new Stage(gameViewport);
        tooltipTable = new Table();

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        // 例：浅黄背景，可按需修改 RGB/alpha
        pix.setColor(1f, 1f, 220f / 255f, 1f);
        pix.fill();
        Texture tooltipBgTexture = new Texture(pix);
        pix.dispose();
        tooltipTable.setBackground(new TextureRegionDrawable(new TextureRegion(tooltipBgTexture)));


        sunLabel = setupUI(); // setupUI方法会创建UI元素并返回sunLabel的引用

        // 4. 初始化游戏网格及参数
        gridCells = new Rectangle[5][9];
        initializeGrid();
//        initializeLawnMowers();
//        for (LawnMower l: lawnMowers) l.activate();
    }

    /**
     * 初始化所有UI元素
     *
     * @return 返回sunLabel的引用，方便后续更新
     */
    private Label setupUI() {
        topBar = new Table(); // 使用Table来布局UI
        topBar.top().left(); // 对齐到舞台的左上角
        topBar.setPosition(cameraMinX, startY + 6 * cellHeight - 15);

        // 创建太阳标签
        Label label = new Label(String.valueOf(gameManager.getSun()), uiSkin);
        label.setColor(Color.BLACK);
        label.setFontScale(.9f);
        label.setAlignment(Align.center);
        topBar.add(label).width(40).padTop(70).padLeft(20).padRight(15);


        // 创建植物卡片
        PlantCard peashooterCard = new PlantCard(this, uiSkin, new Peashooter(this, 0,0));
        PlantCard sunflowerCard = new PlantCard(this, uiSkin, new Sunflower(this, 0,0));
        PlantCard wallnutCard = new PlantCard(this, uiSkin, new Wallnut(this, 0,0));
        PlantCard potatoMineCard = new PlantCard(this, uiSkin, new PotatoMine(this, 0,0));
        PlantCard cherryBombCard = new PlantCard(this, uiSkin, new CherryBomb(this, 0,0));
        PlantCard snowPeaCard = new PlantCard(this, uiSkin, new SnowPeashooter(this, 0, 0));

        plantCards.add(peashooterCard);
        plantCards.add(sunflowerCard);
        plantCards.add(wallnutCard);
        plantCards.add(potatoMineCard);
        plantCards.add(cherryBombCard);
        plantCards.add(snowPeaCard);

        // 将卡片添加到Table中
        topBar.add(peashooterCard).pad(1).padBottom(3).bottom();
        topBar.add(sunflowerCard).pad(1).padBottom(3).bottom();
        topBar.add(wallnutCard).pad(1).padBottom(3).bottom();
        topBar.add(potatoMineCard).pad(1).padBottom(3).bottom();
        topBar.add(cherryBombCard).pad(1).padBottom(3).bottom();
        topBar.add(snowPeaCard).pad(1).padBottom(3).bottom();

        shovel = new Shovel();
        shovel.setPosition(cameraMinX + 446, startY + 5 * cellHeight);

        pauseButton = new PvzGraveButton("Menu");
        pauseButton.setPosition(cameraMaxX - pauseButton.getWidth(), cameraMaxY - pauseButton.getHeight());
        pauseButton.setClickListener(this::pauseOnMenu);


        uiStage.addActor(topBar);
        uiStage.addActor(tooltipTable);
        uiStage.addActor(shovel);
        uiStage.addActor(pauseButton);

        topBar.setVisible(false);
        shovel.setVisible(false);
        pauseButton.setVisible(false);

        tooltipTable.pack();
        return label;
    }

    /**
     * 初始化草坪的逻辑网格
     */
    private void initializeGrid() {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                gridCells[row][col] = new Rectangle(startX + col * cellWidth, startY + row * cellHeight, cellWidth, cellHeight);
            }
        }
    }

    private void initializeLawnMowers() {
        Texture lawnMowerTexture = AssetTexture.LAWNMOWER.get(assetManager);
        for (int row = 0; row < 5; row++) {
            float yPosition = gridCells[row][0].y;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    lawnMowers.add(new LawnMower(lawnMowerTexture, startX - 2 * cellWidth, yPosition + 15));
                }
            }, row * .1f);
        }
    }

    private void readyingGame() {
        Skin skin = AssetSkin.PVZ_CUSTOM_SKIN.get(assetManager);
        Label readySetPlant = new Label("READY...", skin.get("ReadySetPlant", Label.LabelStyle.class));
        readySetPlant.setWidth(800);
        readySetPlant.setAlignment(Align.center);
        readySetPlant.setFontScale(2f);
        readySetPlant.setPosition(cameraMinX,
            gameCamera.position.y - readySetPlant.getHeight() / 2f);
        uiStage.addActor(readySetPlant);

        initializeLawnMowers();

        soundManager.playSound(AssetSound.READY_SET_PLANT);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                readySetPlant.setFontScale(1.5f);
                readySetPlant.setText("SET...");
            }
        }, 0.5f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                readySetPlant.setFontScale(3f);
                readySetPlant.setText("PLANT!");
            }
        }, 1f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                gameManager.setGameState(GameManager.GameState.PLAYING);
                if (gameManager.isCrazyMode()) {
                    soundManager.changeMusic(AssetMusic.THE_KING_PAPERMAN, true);
                } else {
                    soundManager.changeMusic(AssetMusic.GRASSWALK, true);
                }
                displayFullUI();
                readySetPlant.setVisible(false);
            }
        }, 2.5f);
    }

    private void displayFullUI() {
        topBar.setVisible(true);
        shovel.setVisible(true);
        pauseButton.setVisible(true);
    }

    /**
     * 渲染方法，每帧调用一次
     * 绘制顺序：背景 -> ui -> 游戏对象 -> 阳光等特殊UI -> 种植预览 -> 粒子和对话框 -> 暂停窗口（如果是暂停状态）
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {

        applyCameraMove(delta);
        applyCameraShake(delta);

        if (gameManager.getGameState() == GameManager.GameState.PREVIEWING) {
            moveCameraToXSmooth(cameraMinX + 9f * cellWidth, 2f);
            if (!isCameraMoving) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        gameManager.setGameState(GameManager.GameState.CHOOSING);
                    }
                }, 1f);
            }
        }

        if (gameManager.getGameState() == GameManager.GameState.CHOOSING) {
            // Todo: 选卡页面
            moveCameraToXSmooth((cameraMinX + cameraMaxX) / 2f, 2f);
            if (!isCameraMoving) {
                gameManager.setGameState(GameManager.GameState.READYING);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        soundManager.pauseMusic();
                        readyingGame();
                    }
                }, 1f);

            }
        }

        if (gameManager.getGameState() == GameManager.GameState.READYING) {
            for (LawnMower lawnMower : lawnMowers) {
                if (lawnMower.getPosition().x <= startX - cellWidth) {
                    lawnMower.getPosition().x += 2 * cellWidth * delta; // 平滑移动到位置
                }
            }
        }


        // 1. 清理屏幕
        Gdx.gl.glClearColor(0.3f, 0.7f, 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 2. 更新游戏逻辑 (仅在游戏进行时)
        if (gameManager.getGameState() == GameManager.GameState.PLAYING || gameManager.getGameState() == GameManager.GameState.GAME_OVER) {
            updateGame(delta);
        }

        // 3. 渲染游戏世界
        gameViewport.apply(); // 应用游戏视口
        gameCamera.update();
        game.batch.setProjectionMatrix(gameCamera.combined); // 设置投影矩阵

        game.batch.begin();
        // 绘制背景
        game.batch.draw(AssetTexture.MAP_0.get(assetManager), 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        if (gameTime > 0f) game.batch.draw(AssetTexture.SEED_BANK.get(assetManager), cameraMinX, startY + 5 * cellHeight - 15); // 绘制种子银行背景
        // 绘制所有游戏对象

        game.batch.end();

        // 绘制种植预览
        int screenX = Gdx.input.getX();
        int screenY = Gdx.input.getY();
        Vector2 worldCoords = gameViewport.unproject(new Vector2(screenX, screenY));


//        shapeRenderer.setProjectionMatrix(gameCamera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.RED);
//        for (BaseZombie z: zombies) {
//            Rectangle r = z.getCenterBounds();
//            shapeRenderer.rect(r.x, r.y, r.width, r.height);
//        }
//        for (BasePlant p: plants) {
//            Rectangle r = p.getBounds();
//            shapeRenderer.rect(r.x, r.y, r.width, r.height);
//        }
//        shapeRenderer.end();

        // 4. 渲染UI
        sunLabel.setText(String.valueOf(gameManager.getSun())); // 更新UI标签
        uiStage.getViewport().apply(); // 应用UI视口
        uiStage.draw();     // 绘制UI舞台

        game.batch.begin();

        for (BasePlant plant : plants) plant.drawShadow(game.batch);
        for (BaseZombie zombie : zombies) zombie.drawShadow(game.batch);
        for (BaseProjectile projectile : projectiles) projectile.drawShadow(game.batch);
        for (BasePlant plant : plants) plant.drawAnimation(game.batch);
        for (BaseZombie zombie : zombies) zombie.drawAnimation(game.batch);
        for (BaseProjectile projectile : projectiles) projectile.draw(game.batch);
        for (LawnMower lawnMower : lawnMowers) lawnMower.draw(game.batch);
        for (Falling falling : fallings) falling.draw(game.batch);

        if (selectedCard != null) {
            Texture t = selectedCard.getPlantTexture();
            Vector2 v = selectedCard.getPlantPosition();
            if (canPlantAt(highlightedRow, highlightedCol)) {
                Rectangle cell = gridCells[highlightedRow][highlightedCol];
                game.batch.setColor(1, 1, 1, 0.5f); // 半透明
                game.batch.draw(t, cell.x + v.x, cell.y + v.y);
                game.batch.setColor(1, 1, 1, 1); // 恢复不透明
            }
            game.batch.draw(t, worldCoords.x - t.getWidth() / 2f, worldCoords.y - t.getHeight() / 2f);
        } else if (shovel.isSelected()) {
            for (BasePlant plant : plants) plant.setHighlighted(plant.getBounds().contains(worldCoords.x, worldCoords.y));
            Texture t = shovel.getShovelTexture();
            game.batch.draw(t, worldCoords.x - t.getWidth() / 2f, worldCoords.y - t.getHeight() / 2f);
        }

        for (ParticleEffect particleEffect : particles) particleEffect.draw(game.batch);
        game.batch.end();

        Batch batch = uiStage.getBatch();
        batch.setProjectionMatrix(uiStage.getCamera().combined);

        batch.begin();
        for (Sun s : suns) s.draw(batch, 1f);
        for (GameDialog dialog : gameDialogs) dialog.getDialogTable().draw(batch, 1f);
        if (gameManager.getGameState() == GameManager.GameState.PAUSED) {
            for (Group modal : modalStack) modal.draw(batch, 1f);
        }
        batch.end();

//        shapeRenderer.setProjectionMatrix(gameCamera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(1, 0, 0, 1); // 红色边框
//        for (int row = 0; row < 5; row++) {
//            for (int col = 0; col < 9; col++) {
//                Rectangle cell = gridCells[row][col];
//                shapeRenderer.rect(cell.x, cell.y, cell.width, cell.height);
//            }
//        }
//        shapeRenderer.end();

        if (gameManager.getGameState() == GameManager.GameState.STOPPED) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            // 游戏结束时的逻辑（如果需要）
            endBlackOverlayAlpha += delta * 0.2f;
            if (endBlackOverlayAlpha > 1f) endBlackOverlayAlpha = 1f;

            shapeRenderer.setProjectionMatrix(gameCamera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, endBlackOverlayAlpha); // 设置黑色和透明度
            shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT); // 绘制全屏矩形
            shapeRenderer.end();

            game.batch.begin();
            Texture gameOverTexture = AssetTexture.ZOMBIES_ATE_YOUR_BRAINS.get(assetManager);
            // 添加抖动效果

            endWordScale += delta * 0.4f;
            if (endWordScale > 1f) endWordScale = 1f;
            float centerX = gameCamera.position.x - (gameOverTexture.getWidth() * endWordScale) / 2f;
            float centerY = gameCamera.position.y - (gameOverTexture.getHeight() * endWordScale) / 2f;


            Random random = new Random();
            float shakeOffsetX = (random.nextFloat() - 0.5f) * 5f * endWordScale; // 随机偏移范围为 [-10, 10]
            float shakeOffsetY = (random.nextFloat() - 0.5f) * 5f * endWordScale; // 随机偏移范围为 [-10, 10]



            game.batch.draw(gameOverTexture, centerX + shakeOffsetX, centerY + shakeOffsetY,
                gameOverTexture.getWidth() * endWordScale, gameOverTexture.getHeight() * endWordScale);
            game.batch.end();
        }
    }

    public void triggerCameraShake(float duration, float magnitude) {
        if (duration <= 0f || magnitude <= 0f) return;
        this.cameraShakeDuration = duration;
        this.cameraShakeTime = duration;
        this.cameraShakeMagnitude = magnitude;
        // 记录触发时的相机位置（支持在相机移动时触发）
        this.cameraShakeOrigin.set(gameCamera.position.x, gameCamera.position.y);
    }

    private void moveCameraToX(float targetX, float lerpSpeed) {
        if (isCameraMoving) return; // 如果相机正在移动，忽略新的移动请求
        if (Math.abs(gameCamera.position.x - targetX) <= 1f) {
            gameCamera.position.x = targetX;
            gameCamera.update();
            return;
        }
        this.cameraTargetX = targetX;
        this.cameraLerpSpeed = lerpSpeed;
        this.isCameraMoving = true;
        // 退出平滑模式，避免与时长模式冲突
        this.cameraSmoothMode = false;
        this.cameraMoveDuration = 0f;
        this.cameraMoveElapsed = 0f;
    }

    public void moveCameraToXSmooth(float targetX, float durationSeconds) {
        if (isCameraMoving) return; // 如果相机正在移动，忽略新的移动请求
        if (Math.abs(gameCamera.position.x - targetX) <= 0.01f || durationSeconds <= 0f) {
            gameCamera.position.x = targetX;
            gameCamera.update();
            // 清理状态
            this.isCameraMoving = false;
            this.cameraSmoothMode = false;
            this.cameraMoveDuration = 0f;
            this.cameraMoveElapsed = 0f;
            return;
        }

        this.cameraStartX = gameCamera.position.x;
        this.cameraTargetX = targetX;
        this.cameraMoveDuration = Math.max(0.0001f, durationSeconds);
        this.cameraMoveElapsed = 0f;
        this.cameraSmoothMode = true;
        this.isCameraMoving = true;
    }

    private void applyCameraMove(float delta) {
        if (!isCameraMoving) return;

        if (cameraSmoothMode && cameraMoveDuration > 0f) {
            cameraMoveElapsed += delta;
            float t = cameraMoveElapsed / cameraMoveDuration;
            if (t >= 1f) {
                // 结束平滑移动
                gameCamera.position.x = cameraTargetX;
                isCameraMoving = false;
                cameraSmoothMode = false;
                cameraMoveDuration = 0f;
                cameraMoveElapsed = 0f;
            } else {
                // easing: smoothstep（更平滑的加速/减速）
                float eased = t * t * (3f - 2f * t);
                gameCamera.position.x = MathUtils.lerp(cameraStartX, cameraTargetX, eased);
            }
            gameCamera.update();
        } else {
            // 速率模式（保持旧逻辑）
            gameCamera.position.x += cameraLerpSpeed * delta;
            boolean reached;
            if (cameraLerpSpeed >= 0f) {
                reached = gameCamera.position.x >= cameraTargetX;
            } else {
                reached = gameCamera.position.x <= cameraTargetX;
            }
            if (reached || Math.abs(gameCamera.position.x - cameraTargetX) <= 1f) {
                gameCamera.position.x = cameraTargetX;
                isCameraMoving = false;
            }
            gameCamera.update();
        }
    }

    // 新增每帧应用抖动的方法（在 render 或 updateGame 中调用）
    private void applyCameraShake(float delta) {
        if (cameraShakeTime > 0f) {
            cameraShakeTime -= delta;
            float progress = MathUtils.clamp(cameraShakeTime / cameraShakeDuration, 0f, 1f);
            // 线性衰减幅度（可改成曲线）
            float currentMag = cameraShakeMagnitude * progress;
            float offsetX = (MathUtils.random() - 0.5f) * 2f * currentMag;
            float offsetY = (MathUtils.random() - 0.5f) * 2f * currentMag;
            gameCamera.position.set(cameraShakeOrigin.x + offsetX, cameraShakeOrigin.y + offsetY, 0f);
            if (cameraShakeTime <= 0f) {
                // 恢复到触发时的精确位置，避免累积误差
                gameCamera.position.set(cameraShakeOrigin.x, cameraShakeOrigin.y, 0f);
            }
            gameCamera.update();
        }
    }

    /**
     * 封装了所有游戏世界的更新逻辑
     */
    private void updateGame(float delta) {
        gameTime += delta;
        // 更新所有游戏对象
        for (BasePlant plant : plants) plant.update(delta);
        for (BaseZombie zombie : zombies) zombie.update(delta);
        for (BaseProjectile projectile : projectiles) { projectile.update(delta); }
        for (LawnMower lawnMower : lawnMowers) lawnMower.update(delta);
        for (Falling falling : fallings) falling.update(delta);
        for (ParticleEffect particleEffect : particles) particleEffect.update(delta);


        if (gameManager.isCrazyMode() && gameTime >= 41f) {
            beginCrazyMode();
        }

        // 计时生成僵尸
        zombieSpawnTimer += delta;
        if (zombieSpawnTimer > ZOMBIE_SPAWN_INTERVAL) { // 每8秒生成一个
            spawnZombie();
            zombieSpawnTimer = 0;
        }

        sunSpawnTimer += delta;
        if (sunSpawnTimer > SUN_SPAWN_INTERVAL) { // 每12秒生成
            spawnFallingSun();
            sunSpawnTimer = 0f;
        }

        if (gameManager.isAutoCollect()) {
            for (Sun sun : suns) {
                if (sun.getState() == Sun.SunState.ON_GROUND) {
                    collectSun(sun);
                }
            }
        }

        // 执行碰撞检测
        checkCollisions();

        // 移除死亡或不活跃的对象
        cleanupObjects();

        // 检查游戏是否结束
        checkGameOver();

        uiStage.act(delta); // 更新UI舞台（例如，执行Action）
    }

    private void spawnFallingSun() {
        float randomX = MathUtils.random(cameraMinX, cameraMaxX - 50f);
        float randomGroundY = MathUtils.random(50f, WORLD_HEIGHT - 200f);
        Sun sun = new Sun(AssetTexture.SUN_STILL.get(assetManager), AssetAnimation.SUN.get(assetManager), randomX, WORLD_HEIGHT, randomGroundY);
        suns.add(sun);
        uiStage.addActor(sun);
    }

    public void spawnGeneratedSun(int value, float x, float y) {
        Sun sun;
        if (gameManager.isCrazyMode()) {
            sun = DancingSun.generatedDancingSun(value, AssetTexture.SUN_STILL.get(assetManager), AssetAnimation.SUN.get(assetManager), x, y, 0);
        } else {
            sun = Sun.PlantGeneratedSun(value, AssetTexture.SUN_STILL.get(assetManager), AssetAnimation.SUN.get(assetManager), x, y, y - 30);
        }
        suns.add(sun);
        uiStage.addActor(sun);
    }

    /**
     * 在随机一行生成一个僵尸
     */
    private void spawnZombie() {
        int row = MathUtils.random(0, 4);
        if (gameTime >= 60f) {
            float r = MathUtils.random();
            if (r < 0.5f) {
                zombies.add(new NormalZombie(this, cameraMaxX + 20f, gridCells[row][0].y));
            } else if (r < 0.8f) {
                zombies.add(new ConeHeadZombie(this, cameraMaxX + 20f, gridCells[row][0].y));
            } else {
                zombies.add(new BucketHeadZombie(this, cameraMaxX + 20f, gridCells[row][0].y));
            }
            return;
        } else if (gameTime >= 30f) {
            float r = MathUtils.random();
            if (r < 0.5f) {
                zombies.add(new NormalZombie(this, cameraMaxX + 20f, gridCells[row][0].y));
            } else {
                zombies.add(new ConeHeadZombie(this, cameraMaxX + 20f, gridCells[row][0].y));
            }
        } else {
            zombies.add(new NormalZombie(this, cameraMaxX + 20f, gridCells[row][0].y));
        }
        if (!firstZombieSpawned) {
            firstZombieSpawned = true;
            soundManager.playSound(AssetSound.FIRST_ZOMBIE);
        }
    }

    public boolean anyZombieInHeight(float height) {
        for (BaseZombie z : zombies) {
            float zombieX = z.getCenterBounds().getX();
            float zombieY = z.getY();
            if (zombieY >= height - 10 && zombieY <= height + 10 && zombieX <= cameraMaxX && zombieX >= cameraMinX) {
                return true;
            }
        }
        return false;
    }

    public void addFalling(Falling falling) {
        fallings.add(falling);
    }

    public void addParticle(ParticleEffect particle) {
        particles.add(particle);
    }

    /**
     * 检查游戏结束条件
     */
    private void checkGameOver() {
        if (gameManager.getGameState() == GameManager.GameState.GAME_OVER) {
            return; // 已经是游戏结束状态，跳过检查
        }
        for(BaseZombie z : zombies) {
            float zombieX = z.getCenterBounds().getX();
            if(zombieX < startX - 20f && zombieX >= cameraMinX) { // 如果有僵尸走到了屏幕最左边
                startGameOver(z);
            }
        }
    }

    private void startGameOver(BaseZombie killer) {
        gameManager.setGameState(GameManager.GameState.GAME_OVER);
        killer.moveToDoor(1.5f, startY + 1.5f * cellHeight); // 让这个僵尸走向大门
        soundManager.changeMusic(AssetMusic.LOSE_MUSIC, false);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                moveCameraToX(startX + 3.5f * cellWidth, 50f);
            }
        }, 2f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                killer.stand();
                killer.setAnimation(AssetAnimation.NORMAL_ZOMBIE_ATTACK.get(assetManager));
                soundManager.playSound(AssetSound.CHOMP);
            }
        }, 5f, .5f, 5);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                soundManager.playSound(AssetSound.DAVE_SCREAM);
                gameManager.setGameState(GameManager.GameState.STOPPED);
            }
        }, 7.5f);


    }

    /**
     * 负责处理所有碰撞逻辑
     */
    private void checkCollisions() {
        // 1. 子弹与僵尸的碰撞
        for (BaseProjectile p : projectiles) {
            for (BaseZombie z : zombies) {
                // 如果子弹的矩形和僵尸的矩形重叠
                if (p.getBounds().overlaps(z.getCenterBounds())) {
                    p.damage(z);
                }
            }
        }

        // 2. 僵尸与植物的碰撞
        for (BaseZombie z : zombies) {
            boolean isCurrentlyAttacking = false;
            for (BasePlant p : plants) {
                // 如果僵尸的矩形和植物的矩形重叠
                if (z.getCenterBounds().overlaps(p.getBounds())) {
                    z.startAttacking(p); // 让僵尸进入攻击模式
                    isCurrentlyAttacking = true;
                    break; // 一个僵尸一次只攻击一个植物
                }
            }
            // 如果这个僵尸遍历完所有植物都没有发生碰撞，说明它不在攻击范围内
            if (!isCurrentlyAttacking) {
                z.stopAttacking(); // 让僵尸退出攻击模式，继续前进
            }
        }

        // 3. 僵尸与割草机的碰撞
        for (LawnMower lm : lawnMowers) {
            if (lm.getState() == LawnMower.LawnMowerState.READY) {
                for (BaseZombie z : zombies) {
                    if (lm.getBounds().overlaps(z.getCenterBounds())) {
                        lm.activate();
                        break; // 一个割草机一次只启动一次
                    }
                }
            }
        }

        // 4. 割草机与僵尸的碰撞
        for (LawnMower lm : lawnMowers) {
            if (lm.getState() == LawnMower.LawnMowerState.ACTIVATED) {
                for (BaseZombie z : zombies) {
                    if (lm.getBounds().overlaps(z.getCenterBounds())) {
                        z.startDying();
                    }
                }
            }
        }
    }

    /**
     * 从列表中移除所有被标记为“死亡”或“不活跃”的对象
     */
    private void cleanupObjects() {
        // 使用迭代器安全地移除植物
        for (Iterator<BasePlant> it = plants.iterator(); it.hasNext();) {
            if (!it.next().alive) {
                it.remove();
            }
        }
        // 移除僵尸
        for (Iterator<BaseZombie> it = zombies.iterator(); it.hasNext();) {
            if (it.next().isRemovable()) {
                it.remove();
            }
        }
        // 移除子弹
        for (Iterator<BaseProjectile> it = projectiles.iterator(); it.hasNext();) {
            if (!it.next().active) {
                it.remove();
            }
        }
        // 移除太阳
        for (Iterator<Sun> it = suns.iterator(); it.hasNext();) {
            if (!it.next().active) {
                it.remove();
            }
        }
        // 移除割草机（如果需要）
        for (Iterator<LawnMower> it = lawnMowers.iterator(); it.hasNext();) {
            if (!it.next().active) {
                it.remove();
            }
        }

        for (Iterator<Falling> it = fallings.iterator(); it.hasNext();) {
            if (!it.next().isActive()) {
                it.remove();
            }
        }

        for (Iterator<ParticleEffect> it = particles.iterator(); it.hasNext();) {
            ParticleEffect p = it.next();
            if (p.isComplete()) {
                p.dispose();
                it.remove();
            }
        }

        for (Iterator<GameDialog> it = gameDialogs.iterator(); it.hasNext();) {
            GameDialog dialog = it.next();
            if (dialog.hasShown()) {
                it.remove();
            }
        }
    }


    private void updateTooltipTable(PlantCard card) {
        tooltipTable.clear();
        if (card.isOnCooldown()) {
            Label cooldownLabel = new Label("recharging...", uiSkin);
            cooldownLabel.setColor(Color.RED);
            cooldownLabel.setFontScale(.6f);
            tooltipTable.add(cooldownLabel).row();
        } else if (!card.isSunEnough()) {
            Label noSunLabel = new Label("not enough sun", uiSkin);
            noSunLabel.setColor(Color.RED);
            noSunLabel.setFontScale(.6f);
            tooltipTable.add(noSunLabel).row();
        }
        Label nameLabel = new Label(card.getPlantType(), uiSkin);
        nameLabel.setColor(Color.BLACK);
        nameLabel.setFontScale(.6f);
        tooltipTable.add(nameLabel).row();

        tooltipTable.pack();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // --- 第一步：处理UI输入（舞台与游戏共用 viewport） ---
        Vector2 uiCoords = new Vector2(screenX, screenY);
        uiStage.getViewport().unproject(uiCoords);

        // 同步计算世界坐标，供种植判定
        Vector2 worldCoords = new Vector2(screenX, screenY);
        gameViewport.unproject(worldCoords);

        for (Sun sun : suns) {
            if (sun.getBounds().contains(uiCoords.x, uiCoords.y)) {
                collectSun(sun);
                return true; // 事件被UI处理，结束
            }
        }

        Actor hitActor = uiStage.hit(uiCoords.x, uiCoords.y, true); // 检测是否点中UI演员
        if (hitActor instanceof PlantCard) {
            PlantCard card = (PlantCard) hitActor;
            tryLiftSeedCard(card);
            return true; // 事件被UI处理，结束
        } else if (hitActor instanceof Shovel) {
            tryLiftShovel();
            return true; // 事件被UI处理，结束
        }

        // --- 第二步：如果UI未处理，则处理游戏世界输入 ---
        if (selectedCard != null || shovel.isSelected()) {
            // 遍历所有网格，检查点击位置（使用世界坐标）
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 9; col++) {
                    Rectangle cell = gridCells[row][col];
                    if (shovel.isSelected() && cell.contains(worldCoords.x, worldCoords.y)) {
                        removePlantAt(cell);
                        return true; // 事件被游戏世界处理，结束
                    }
                    if (cell.contains(worldCoords.x, worldCoords.y) && canPlantAt(cell)) {
                        placePlant(cell);
                        return true; // 事件被游戏世界处理，结束
                    }
                }
            }
        }

        return false; // 点击了空白区域，事件未被处理
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    private boolean canPlantAt(int row, int col) {
        if (row < 0 || row >= 5 || col < 0 || col >= 9) {
            return false; // 超出网格范围
        }
        Rectangle cell = gridCells[row][col];
        // 检查该格子是否已有植物
        for (BasePlant plant : plants) {
            if (plant.getBounds().overlaps(cell)) {
                return false; // 该格子已有植物
            }
        }
        return true; // 可以种植
    }

    private boolean canPlantAt(Rectangle cell) {
        // 检查该格子是否已有植物
        for (BasePlant plant : plants) {
            if (plant.getBounds().overlaps(cell)) {
                return false; // 该格子已有植物
            }
        }
        return true; // 可以种植
    }

    private void tryLiftSeedCard(PlantCard card) {
        if (shovel.isSelected()) {
            shovel.deselect();
            soundManager.playSound(AssetSound.TAP2);
            return; // 铲子处于选中状态，取消铲子选择
        }
        if (selectedCard != null) {
            selectedCard.deselect();
            selectedCard = null;
            soundManager.playSound(AssetSound.TAP2);
            return; // 已有选中卡片，取消选择
        }
        if (!card.isSunEnough() || card.isOnCooldown()) {
            soundManager.playSound(AssetSound.BUZZER);
            return; // 太阳不够，点击无效
        }
        card.select();
        selectedCard = card; // 选中新卡片
        soundManager.playSound(AssetSound.SEED_LIFT);
    }

    private void tryLiftShovel() {
        if (selectedCard != null) {
            selectedCard.deselect();
            selectedCard = null;
            soundManager.playSound(AssetSound.TAP2);
        } else if (shovel.isSelected()) {
            shovel.deselect();
        } else {
            shovel.select();
        }
    }

    public void collectSun(Sun sun) {
        if (sun.getState() == Sun.SunState.COLLECTED) {
            return; // 已经在收集中了，忽略重复点击
        }
        soundManager.playSound(AssetSound.COLLECT_SUN);
        sun.collectTo((int) (cameraMinX + 15), (int) (startY + 5 * cellHeight + 15)); // 收集太阳
        gameManager.addSun(sun.getValue());
    }

    @Override
    public int getRow(float y) {
        return (int)((y - startY) / cellHeight);
    }

    @Override
    public int getCol(float x) {
        return (int)((x - startX) / cellWidth);
    }

    @Override
    public void addGameDialog(String message, float duration) {
        GameDialog gameDialog = new GameDialog(uiSkin, message,
            gameCamera.position.x, gameCamera.position.y, gameCamera.viewportWidth, gameCamera.viewportHeight, duration);
        gameDialogs.add(gameDialog);
    }

    /**
     * 在指定的格子种植当前选中的植物
     * @param cell 目标格子
     */
    private void placePlant(Rectangle cell) {
        // 尝试花费太阳
        if (gameManager.spendSun(selectedCard.getCost())) {
            BasePlant newPlant = BasePlant.create(selectedCard.getPlantClass(), this, cell.x, cell.y);
            soundManager.playRandomSound(Arrays.asList(AssetSound.PLANT_1, AssetSound.PLANT_2));
            ParticleEffect plantEffect = AssetParticle.PLANTING.get(assetManager);
            plantEffect.setPosition(cell.x + cell.width / 2f, cell.y + 5);
            plantEffect.scaleEffect(.4f);
            plantEffect.start();
            particles.add(plantEffect);
            plants.add(newPlant); // 将新植物添加到世界中
            selectedCard.resetCooldown();
        }

        // 无论种植成功与否，都取消卡片的选择状态
        selectedCard.deselect();
        selectedCard = null;
    }

    private void removePlantAt(Rectangle cell) {
        for (Iterator<BasePlant> it = plants.iterator(); it.hasNext();) {
            BasePlant plant = it.next();
            if (plant.getBounds().overlaps(cell)) {
                soundManager.playSound(AssetSound.PLANT_2);
                it.remove();
                break; // 一次只移除一个植物
            }
        }
        shovel.deselect();
    }

    @Override public boolean mouseMoved(int screenX, int screenY) {
        boolean foundHighlightedCell = false;
        Vector2 worldCoords = new Vector2(screenX, screenY);
        gameViewport.unproject(worldCoords); // 将屏幕坐标转换为游戏世界坐标

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                Rectangle cell = gridCells[row][col];
                if (cell.contains(worldCoords.x, worldCoords.y)) {
                    highlightedRow = row;
                    highlightedCol = col;
                    foundHighlightedCell = true;
                    break;
                }
            }
            if (foundHighlightedCell) break;
        }
        if (!foundHighlightedCell) {
            // 如果没有高亮任何格子，重置索引
            highlightedRow = -1;
            highlightedCol = -1;
        }

        Vector2 uiCoords = new Vector2(screenX, screenY);
        uiStage.getViewport().unproject(uiCoords);

        Actor hitActor = uiStage.hit(uiCoords.x, uiCoords.y, true); // 检测是否点中UI演员
        if (hitActor instanceof PlantCard) {
            PlantCard card = (PlantCard) hitActor;
            updateTooltipTable(card);

            // card 在 stage 的左下角坐标
            Vector2 stagePos = new Vector2(0f, 0f);
            card.localToStageCoordinates(stagePos);

            float pw = tooltipTable.getWidth();
            float ph = tooltipTable.getHeight();

            // 居中放在卡片正下方
            float tx = stagePos.x + card.getWidth() / 2f - pw / 2f;
            float ty = stagePos.y - ph - 6f;

            // 边界限制
            if (tx < 0f) tx = 0f;
            if (ty < 0f) ty = 0f;

            tooltipTable.setPosition(tx, ty);
            tooltipTable.setVisible(true);
            return true;
        } else if (hitActor instanceof Shovel) {
            tooltipTable.clear();
            Label shovelLabel = new Label("Remove plants", uiSkin);
            shovelLabel.setColor(Color.BLACK);
            shovelLabel.setFontScale(.6f);
            tooltipTable.add(shovelLabel).row();
            tooltipTable.pack();

            float pw = tooltipTable.getWidth();
            float ph = tooltipTable.getHeight();
            // 居中放在铲子正下方
            Vector2 stagePos = new Vector2(0f, 0f);
            shovel.localToStageCoordinates(stagePos);
            float tx = stagePos.x + shovel.getWidth() / 2f - pw / 2f;
            float ty = stagePos.y - ph - 6f;
            // 边界限制
            float maxX = uiStage.getViewport().getWorldWidth() - pw;
            if (tx < 0f) tx = 0f;
            if (tx > maxX) tx = maxX;
            if (ty < 0f) ty = 0f;
            tooltipTable.setPosition(tx, ty);
            tooltipTable.setVisible(true);
            return true;
        } else {
            tooltipTable.setVisible(false);
        }
        return foundHighlightedCell;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    // --- 其他 Screen 和 InputProcessor 的方法 ---

    @Override
    public void resize(int width, int height) {
        // 禁止自动居中，避免把相机挪回 worldWidth/2 导致左偏
        gameViewport.update(width, height, false);
        // 手动把相机定位到用户提供的范围中心
        gameCamera.position.set((cameraMinX + cameraMaxX) / 2f, (cameraMinY + cameraMaxY) / 2f, 0f);
        gameCamera.update();
        // 同步 UI 的 viewport
        uiStage.getViewport().update(width, height, false);
    }

    // 让GameScreen能够生成子弹
    public void spawnProjectile(BaseProjectile projectile) {
        projectiles.add(projectile);
        if (projectile.getParticleEffect() != null) {
            projectile.getParticleEffect().start();
            particles.add(projectile.getParticleEffect());
        }
    }

    public void doAoeDamage(Rectangle area, int damage, DamageType type) {
        Array.ArrayIterator<BaseZombie> iterator = new Array.ArrayIterator<>(zombies);
        while (iterator.hasNext()) {
            BaseZombie z = iterator.next();
            if (area.overlaps(z.getCenterBounds())) {
                z.takeDamage(damage, type);
            }
        }
    }

    public boolean isSunAvailable(int cost) {
        return gameManager.getSun() >= cost;
    }

    @Override
    public void show() {
        // 将本类设置为输入处理器，这样touchDown等方法才会被调用
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);


        soundManager.changeMusic(AssetMusic.CHOOSE_YOUR_SEEDS, true);
        if (gameManager.getGameState() == GameManager.GameState.PAUSED) soundManager.pauseMusic();
    }

    @Override
    public void dispose() {
        soundManager.stopMusic();

        for (ParticleEffect p : particles) {
            p.dispose();
        }
        particles.clear();

        // 3. 释放通过 Pixmap 创建的纹理
        if (whitePixel != null) {
            whitePixel.dispose();
            whitePixel = null;
        }

        uiStage.dispose();
        shapeRenderer.dispose();

        plants.clear();
        zombies.clear();
        projectiles.clear();
        suns.clear();
        lawnMowers.clear();
        fallings.clear();
        gameDialogs.clear();
        plantCards.clear();

        Timer.instance().clear();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.NUM_1) {
            tryLiftSeedCard(plantCards.get(0));
            return true;
        } else if (keycode == Input.Keys.NUM_2) {
            tryLiftSeedCard(plantCards.get(1));
            return true;
        } else if (keycode == Input.Keys.NUM_3) {
            tryLiftSeedCard(plantCards.get(2));
            return true;
        } else if (keycode == Input.Keys.NUM_4) {
            tryLiftSeedCard(plantCards.get(3));
            return true;
        } else if (keycode == Input.Keys.NUM_5) {
            tryLiftSeedCard(plantCards.get(4));
            return true;
        } else if (keycode == Input.Keys.NUM_6) {
            tryLiftSeedCard(plantCards.get(5));
            return true;
        } else if (keycode == Input.Keys.NUM_7) {
            tryLiftSeedCard(plantCards.get(6));
            return true;
        }
        if (keycode == Input.Keys.Q) {
            soundManager.playSound(AssetSound.DIAMOND);
            if (gameManager.isAutoCollect()) {
                addGameDialog("Auto Collect has been closed!", 5f);
            } else {
                addGameDialog("Auto Collect has been opened!", 5f);
            }
            gameManager.setAutoCollect(!gameManager.isAutoCollect());

            return true;
        } else if (keycode == Input.Keys.W) {
            soundManager.playSound(AssetSound.DIAMOND);
            if (gameManager.isCardCooldownOn()) {
                addGameDialog("Card Cooldown has been closed!", 5f);
            } else {
                addGameDialog("Card Cooldown has been opened!", 5f);
            }
            gameManager.setCardCooldown(!gameManager.isCardCooldownOn());
            return true;
        } else if (keycode == Input.Keys.S) {
            soundManager.playSound(AssetSound.DIAMOND);
            addGameDialog("Sun +500!", 5f);
            gameManager.addSun(500);
            return true;
        } else if (keycode == Input.Keys.GRAVE) {
            tryLiftShovel();
            return true;
        } else if (keycode == Input.Keys.SPACE) {
            if (gameManager.getGameState() == GameManager.GameState.PAUSED) {
                resume();
            } else if (gameManager.getGameState() == GameManager.GameState.PLAYING) {
                pauseOnDialog();
            }
            return true;
        } else if (keycode == Input.Keys.ESCAPE) {
            if (gameManager.getGameState() == GameManager.GameState.PAUSED) {
                resume();
            } else if (gameManager.getGameState() == GameManager.GameState.PLAYING) {
                pauseOnMenu();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
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
    @Override
    public void showModal(Actor dialog, float overlayAlpha) {
        ensureWhitePixel();
        // 创建新层并压入栈
        Group layer = new Group();
        float w = uiStage.getViewport().getWorldWidth();
        float h = uiStage.getViewport().getWorldHeight();
        layer.setSize(w, h);
        layer.setPosition(cameraMinX, cameraMinY);
        layer.setTouchable(Touchable.enabled);
        Image bg = new Image(whitePixel);
        bg.setSize(w, h);
        bg.setColor(0f, 0f, 0f, overlayAlpha);
        bg.setTouchable(Touchable.enabled);
        bg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Vector2 stageCoords = layer.localToStageCoordinates(new Vector2(x, y));
                Vector2 dialogLocal = dialog.stageToLocalCoordinates(stageCoords);
                Actor hit = dialog.hit(dialogLocal.x, dialogLocal.y, true);
                return hit == null; // 点击空白处拦截
            }
        });
        layer.addActor(bg);
        dialog.setPosition((w - dialog.getWidth()) / 2f, (h - dialog.getHeight()) / 2f);
        layer.addActor(dialog);
        uiStage.addActor(layer);
        modalStack.add(layer);
    }

    @Override
    public void hideModal() {
        if (modalStack.size == 0) return;
        Group top = modalStack.pop();
        top.remove();
    }

    // 提供清除全部（内部使用，可选）
    private void hideAllModals() {
        for (Group g : modalStack) g.remove();
        modalStack.clear();
    }

    /**
     * 重新创建一个新的 GameScreen 实例，替换当前屏幕
     */
    public void restart() {
        hideAllModals();
        gameManager.reset();
        game.setScreen(new GameScreen(game));
    }

    // 补回缺失的方法
    @Override
    public void beginCrazyMode() {
        ZOMBIE_SPAWN_INTERVAL = .5f;
        SUN_SPAWN_INTERVAL = 1.0f;
        gameManager.setCardCooldown(false);
    }
    @Override
    public void pause() {
        if (gameManager.getGameState() == GameManager.GameState.PAUSED ||
            gameManager.getGameState() == GameManager.GameState.GAME_OVER ||
            gameManager.getGameState() == GameManager.GameState.STOPPED) {
            return;
        }
        gameManager.setGameState(GameManager.GameState.PAUSED);
        soundManager.pauseSoundsAndMusic();
    }
    private void pauseOnDialog() {
        pause();
        PvzGraveButton resumeButton = new PvzGraveButton("Resume");
        PvzDialogBox pauseDialog = new PvzDialogBox("Game Paused", "Click to resume game", new PvzGraveButton[]{resumeButton});
        resumeButton.setClickListener(this::resume);
        showModal(pauseDialog, 0f);
        soundManager.playSound(AssetSound.PAUSE);
    }
    private void pauseOnMenu() {
        pause();
        PvzMenuPane menu = new PvzMenuPane(game, this);
        showModal(menu, 0f);
        soundManager.playSound(AssetSound.PAUSE);
    }
    @Override
    public void resume() {
        if (gameManager.getGameState() != GameManager.GameState.PAUSED) return;
        gameManager.setGameState(GameManager.GameState.PLAYING);
        hideModal();
        soundManager.resumeSoundsAndMusic();
    }

    @Override public void hide() {}
}
