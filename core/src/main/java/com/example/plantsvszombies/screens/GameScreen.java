package com.example.plantsvszombies.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.example.plantsvszombies.Animation.AnimationClip;
import com.example.plantsvszombies.PvzGame;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.managers.GameManager;
import com.example.plantsvszombies.models.LawnMower;
import com.example.plantsvszombies.models.Plants.*;
import com.example.plantsvszombies.models.Projectiles.BaseProjectile;
import com.example.plantsvszombies.models.Sun;
import com.example.plantsvszombies.models.Texture.Falling;
import com.example.plantsvszombies.models.Zombies.BaseZombie;
import com.example.plantsvszombies.models.Zombies.NormalZombie;
import com.example.plantsvszombies.ui.PlantCard;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameScreen implements Screen, InputProcessor {

    // --- 核心引用 ---
    private final PvzGame game; // 对主游戏类的引用，用于访问全局对象如SpriteBatch
    private final AssetMan assetManager;
    private final GameManager gameManager;

    // --- 渲染相关 ---
    private final OrthographicCamera gameCamera; // 游戏世界的相机
    private final FitViewport gameViewport;     // 保持游戏世界比例的视口
    public static final float WORLD_WIDTH = 1400;  // 游戏世界的宽度
    public static final float WORLD_HEIGHT = 600; // 游戏世界的高度

    // --- 背景音乐 ---
    private Music bgMusic;

    // 相机相关（坐标缓存为字段，避免在 resize 时丢失）
    private final float cameraMinX;
    private final float cameraMaxX;
    private final float cameraMinY;
    private final float cameraMaxY;

    private float cameraShakeTime = 0f;
    private float cameraShakeDuration = 0f;
    private float cameraShakeMagnitude = 0f;
    private final Vector2 cameraShakeOrigin = new Vector2();

    private final float cellWidth = 81;   // 这些数值需要根据你的背景图精确测量
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

    // --- 游戏逻辑 ---
    private float gameTime = 0f;          // 游戏进行的总时间
    private final Rectangle[][] gridCells; // 逻辑网格，用于定位和种植
    private float zombieSpawnTimer;      // 僵尸生成计时器
    private float ZOMBIE_SPAWN_INTERVAL = 8.0f; // 默认僵尸生成间隔
    private float sunSpawnTimer;         // 太阳生成计时器
    private float SUN_SPAWN_INTERVAL = 12.0f; // 太阳生成间隔
    private boolean firstZombieSpawned = false;
    private boolean firstWaveSpawned = false;
    private boolean isCameraMoving = false; // 标记相机是否正在移动

    // --- UI 相关 ---
    private final Skin uiSkin;
    private final Stage uiStage;      // UI专属的舞台
    private final Label sunLabel;     // 显示太阳数量的标签
    private PlantCard selectedCard; // 当前被玩家选中的植物卡片
    private Table tooltipTable;    // 显示卡片信息的提示框

    private float endBlackOverlayAlpha = 0f; // 黑色覆盖层的透明度
    private float endWordScale = 0f; // 抖动计时器

    /**
     * GameScreen的构造函数
     */
    public GameScreen(PvzGame game) {
        this.game = game;
        this.assetManager = AssetMan.getInstance();
        this.gameManager = GameManager.getInstance();

        // 1. 初始化游戏相机和视口
        gameCamera = new OrthographicCamera();
        float minX = startX - cellWidth;
        float maxX = startX + 10 * cellWidth; // 使用你提供的新范围
        float minY = 0;
        float maxY = WORLD_HEIGHT;
        // 缓存范围到字段
        this.cameraMinX = minX;
        this.cameraMaxX = maxX;
        this.cameraMinY = minY;
        this.cameraMaxY = maxY;

        gameViewport = new FitViewport(maxX - minX, maxY - minY, gameCamera);
        gameCamera.position.set((minX + maxX) / 2f, (minY + maxY) / 2f, 0); // 相机居中到可视范围
        gameCamera.update();

        // 2. 初始化游戏对象列表
        plants = new Array<>();
        zombies = new Array<>();
        projectiles = new Array<>();
        suns = new Array<>();
        lawnMowers = new Array<>();
        fallings = new Array<>();

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
        initializeLawnMowers();
//        for (LawnMower l: lawnMowers) l.activate();

        // 5. 将本类设置为输入处理器，这样touchDown等方法才会被调用
        Gdx.input.setInputProcessor(this);
    }

    /**
     * 初始化所有UI元素
     *
     * @return 返回sunLabel的引用，方便后续更新
     */
    private Label setupUI() {
        Table topBar = new Table(); // 使用Table来布局UI
        topBar.top().left(); // 对齐到舞台的左上角
        topBar.setPosition(startX - cellWidth, startY + 6 * cellHeight - 15);

        // 创建太阳标签
        Label label = new Label(String.valueOf(gameManager.getSun()), uiSkin);
        label.setColor(Color.BLACK);
        label.setFontScale(.9f);
        label.setAlignment(Align.center);
        topBar.add(label).width(40).padTop(70).padLeft(20).padRight(15);


        // 创建植物卡片
        PlantCard peashooterCard = new PlantCard(uiSkin, new Peashooter(this, 0,0));
        PlantCard sunflowerCard = new PlantCard(uiSkin, new Sunflower(this, 0,0));
        PlantCard wallnutCard = new PlantCard(uiSkin, new Wallnut(this, 0,0));
        PlantCard potatoMineCard = new PlantCard(uiSkin, new PotatoMine(this, 0,0));

        // 将卡片添加到Table中
        topBar.add(peashooterCard).pad(1).padBottom(3).bottom();
        topBar.add(sunflowerCard).pad(1).padBottom(3).bottom();
        topBar.add(wallnutCard).pad(1).padBottom(3).bottom();
        topBar.add(potatoMineCard).pad(1).padBottom(3).bottom();


        uiStage.addActor(topBar);
        uiStage.addActor(tooltipTable);
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
        Texture lawnMowerTexture = assetManager.getTexture("graphics/Screen/car.png");
        for (int row = 0; row < 5; row++) {
            float yPosition = gridCells[row][0].y;
            lawnMowers.add(new LawnMower(lawnMowerTexture, startX - cellWidth, yPosition + 15));
        }
    }

    @Override
    public void render(float delta) {

        if (isCameraMoving) {
            float targetX = startX + 3.5f * cellWidth;
            float lerpSpeed = 50f; // 控制移动速度
            gameCamera.position.x -= lerpSpeed * delta;

            // 检查是否到达目标位置
            if (gameCamera.position.x <= targetX) {
                gameCamera.position.x = targetX; // 确保精确到达
                isCameraMoving = false; // 停止移动
            }
            gameCamera.update();
        }
        applyCameraShake(delta);


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
        game.batch.draw(assetManager.getTexture("graphics/Map/map0.jpg"), 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        game.batch.draw(assetManager.getTexture("graphics/Screen/SeedBank.png"), startX - cellWidth, startY + 5 * cellHeight - 15, 446, 87); // 绘制种子银行背景
        // 绘制所有游戏对象
        for (BasePlant plant : plants) plant.drawAnimation(game.batch);
        for (BaseZombie zombie : zombies) zombie.drawAnimation(game.batch);
        for (BaseProjectile projectile : projectiles) projectile.draw(game.batch);
        for (LawnMower lawnMower : lawnMowers) lawnMower.draw(game.batch);
        for (Falling falling : fallings) falling.draw(game.batch);

        game.batch.end();

        // 绘制种植预览
        int screenX = Gdx.input.getX();
        int screenY = Gdx.input.getY();
        Vector2 worldCoords = gameViewport.unproject(new Vector2(screenX, screenY));

//        ShapeRenderer shapeRenderer = new ShapeRenderer();
//        shapeRenderer.setProjectionMatrix(gameCamera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.RED);
//        for (BaseZombie z: zombies) {
//            Rectangle r = z.getCenterBounds();
//            shapeRenderer.rect(r.x, r.y, r.width, r.height);
//        }
//        shapeRenderer.end();

        // 4. 渲染UI
        sunLabel.setText(String.valueOf(gameManager.getSun())); // 更新UI标签
        uiStage.getViewport().apply(); // 应用UI视口
        uiStage.act(delta); // 更新UI舞台（例如，执行Action）
        uiStage.draw();     // 绘制UI舞台

        game.batch.begin();

        if (selectedCard != null) {
            Texture t = selectedCard.getPlantTexture();
            if (canPlantAt(highlightedRow, highlightedCol)) {
                Rectangle cell = gridCells[highlightedRow][highlightedCol];
                game.batch.setColor(1, 1, 1, 0.5f); // 半透明
                game.batch.draw(t, cell.x, cell.y);
                game.batch.setColor(1, 1, 1, 1); // 恢复不透明
            }
            game.batch.draw(t, worldCoords.x - t.getWidth() / 2f, worldCoords.y - t.getHeight() / 2f);
        }

        game.batch.end();
//
//        ShapeRenderer shapeRenderer = new ShapeRenderer();
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

            ShapeRenderer shapeRenderer = new ShapeRenderer();
            shapeRenderer.setProjectionMatrix(gameCamera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, endBlackOverlayAlpha); // 设置黑色和透明度
            shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT); // 绘制全屏矩形
            shapeRenderer.end();
            shapeRenderer.dispose();

            game.batch.begin();
            Texture gameOverTexture = assetManager.getTexture("graphics/Screen/ZombiesAteYourBrains.png");
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
        for (BaseProjectile projectile : projectiles) projectile.update(delta);
        for (LawnMower lawnMower : lawnMowers) lawnMower.update(delta);
        for (Falling falling : fallings) falling.update(delta);

//
//        if (gameTime >= 41f) {
//            beginCrazyMode();
//        }

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

        // 执行碰撞检测
        checkCollisions();

        // 移除死亡或不活跃的对象
        cleanupObjects();

        // 检查游戏是否结束
        checkGameOver();
    }

    private void spawnFallingSun() {
        float randomX = MathUtils.random(cameraMinX, cameraMaxX - 50f);
        float randomGroundY = MathUtils.random(50f, WORLD_HEIGHT - 200f);
        Sun sun = new Sun(assetManager.getTexture("graphics/Screen/Sun.gif"), assetManager.getAnimation("Sun"), randomX, WORLD_HEIGHT, randomGroundY);
        suns.add(sun);
        uiStage.addActor(sun);
    }

    public void spawnGeneratedSun(int value, float x, float y) {
        Sun sun = Sun.PlantGeneratedSun(value, assetManager.getTexture("graphics/Screen/Sun.gif"), assetManager.getAnimation("Sun"), x, y, y - 30);
        suns.add(sun);
        uiStage.addActor(sun);
    }

    /**
     * 在随机一行生成一个僵尸
     */
    private void spawnZombie() {
        int row = com.badlogic.gdx.math.MathUtils.random(0, 4);
        // 在屏幕右侧外生成
        Texture zombieTexture = assetManager.getTexture("graphics/Zombies/Zombie/Zombie.gif");
        AnimationClip zombieAnimation = assetManager.getAnimation("NormalZombie");
        zombies.add(new NormalZombie(this, zombieTexture, zombieAnimation, cameraMaxX + 20f, gridCells[row][0].y));
        if (!firstZombieSpawned) {
            firstZombieSpawned = true;
            assetManager.playSound("graphics/audio/awooga.ogg", 0.6f);
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
                // 在实际游戏中，这里应该切换到游戏结束画面
                // game.setScreen(new GameOverScreen(game));
            }
        }
    }

    private void startGameOver(BaseZombie killer) {
        gameManager.setGameState(GameManager.GameState.GAME_OVER);
        killer.moveToDoor(1.5f, startY + 1.5f * cellHeight); // 让这个僵尸走向大门
        bgMusic.stop();
        bgMusic = assetManager.getMusic("graphics/audio/losemusic.ogg");
        bgMusic.setVolume(0.5f);
        bgMusic.play();
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isCameraMoving = true;
            }
        }, 2f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                killer.stand();
                killer.setAnimation(assetManager.getAnimation("NormalZombieAttack"));
                assetManager.playSound("graphics/audio/chomp.ogg", 0.6f);
            }
        }, 5f, .5f, 5);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                assetManager.playSound("graphics/audio/scream.ogg", 0.6f);
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
    }


    private void updateTooltipTable(PlantCard card) {
        tooltipTable.clear();
        if (card.isOnCooldown()) {
            Label cooldownLabel = new Label("recharging...", uiSkin);
            cooldownLabel.setColor(Color.RED);
            cooldownLabel.setFontScale(.6f);
            tooltipTable.add(cooldownLabel).row();
        } else if (gameManager.getSun() < card.getCost()) {
            Label noSunLabel = new Label("not enough sun", uiSkin);
            noSunLabel.setColor(Color.RED);
            noSunLabel.setFontScale(.6f);
            tooltipTable.add(noSunLabel).row();
        }
        Label nameLabel = new Label(card.plantType, uiSkin);
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

        for (Iterator<Sun> it = suns.iterator(); it.hasNext();) {
            Sun sun = it.next();
            if (sun.getBounds().contains(uiCoords.x, uiCoords.y)) {
                assetManager.playSound("graphics/audio/points.ogg", 0.7f);
                sun.collectTo((int) (startX - cellWidth + 15), (int) (startY + 5 * cellHeight + 15)); // 收集太阳
                gameManager.addSun(sun.getValue());
                it.remove();
                return true; // 事件被UI处理，结束
            }
        }

        Actor hitActor = uiStage.hit(uiCoords.x, uiCoords.y, true); // 检测是否点中UI演员
        if (hitActor instanceof PlantCard card) {
            // 如果点中的是植物卡片
            if (selectedCard != null) { // 如果卡片已经被选中，则取消选中
                selectedCard.deselect();
                selectedCard = null;
                assetManager.playSound("graphics/audio/tap2.ogg", 0.5f); // 播放取消选卡音效
            } else {
                if (gameManager.getSun() < card.getCost() || card.isOnCooldown()) {
                    assetManager.playSound("graphics/audio/buzzer.ogg", 0.5f);
                    return true; // 太阳不够，点击无效，事件结束
                }
                card.select();
                selectedCard = card; // 选中新卡片
                assetManager.playSound("graphics/audio/seedlift.ogg", 0.5f); // 播放选卡音效
            }
            return true; // 事件被UI处理，结束
        }

        // --- 第二步：如果UI未处理，则处理游戏世界输入 ---
        if (selectedCard != null) {
            // 遍历所有网格，检查点击位置（使用世界坐标）
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 9; col++) {
                    Rectangle cell = gridCells[row][col];
                    if (cell.contains(worldCoords.x, worldCoords.y) && canPlantAt(cell)) {
                        assetManager.playRandomSound(List.of("graphics/audio/plant1.ogg", "graphics/audio/plant2.ogg"), 0.3f);
                        placePlant(cell);
                        return true; // 事件被游戏世界处理，结束
                    }
                }
            }
        }

        return false; // 点击了空白区域，事件未被处理
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


    /**
     * 在指定的格子种植当前选中的植物
     * @param cell 目标格子
     */
    private void placePlant(Rectangle cell) {
        // 尝试花费太阳
        if (gameManager.spendSun(selectedCard.getCost())) {
            BasePlant newPlant;
            // 根据选中的卡片类型，创建不同的植物实例
            if (selectedCard.plantInstanceOf(Peashooter.class)) {
                newPlant = new Peashooter(this, cell.x, cell.y);
            } else if (selectedCard.plantInstanceOf(Sunflower.class)) {
                newPlant = new Sunflower(this, cell.x, cell.y);
            } else if (selectedCard.plantInstanceOf(Wallnut.class)) {
                newPlant = new Wallnut(this, cell.x, cell.y);
            } else if (selectedCard.plantInstanceOf(PotatoMine.class)) {
                newPlant = new PotatoMine(this, cell.x, cell.y);
            } else {
                return;
            }
            plants.add(newPlant); // 将新植物添加到世界中
            selectedCard.resetCooldown();
        }

        // 无论种植成功与否，都取消卡片的选择状态
        selectedCard.deselect();
        selectedCard = null;
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
        if (hitActor instanceof PlantCard card) {
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

    // --- 其他 Screen 和 InputProcessor 的方法 ---

    @Override
    public void resize(int width, int height) {
        // 禁止自动居中，避免把相机挪回 worldWidth/2 导致左偏
        gameViewport.update(width, height, false);
        // 手动把相机定位到用户提供的范围中心
        gameCamera.position.set((cameraMinX + cameraMaxX) / 2f, (cameraMinY + cameraMaxY) / 2f, 0f);
        gameCamera.update();
        // 同步 UI 的 viewport（与游戏共用对象，这里冗余但安全）
        uiStage.getViewport().update(width, height, false);
    }

    // 让GameScreen能够生成子弹 (被Peashooter调用)
    public void spawnProjectile(BaseProjectile projectile) {
        projectiles.add(projectile);
    }

    public void doAoeDamage(Rectangle area, int damage) {
        Array.ArrayIterator<BaseZombie> iterator = new Array.ArrayIterator<>(zombies);
        while (iterator.hasNext()) {
            BaseZombie z = iterator.next();
            if (area.overlaps(z.getCenterBounds())) {
                z.takeDamage(damage);
            }
        }
    }

    // 让GameScreen能够获取AssetManager (被Peashooter调用)
    public AssetManager getAssetManager() {
        return assetManager.getAssetManager();
    }

    @Override public void show() {
        bgMusic = assetManager.getMusic("graphics/audio/Grasswalk.ogg");
//        bgMusic = assetManager.getMusic("graphics/audio/The-King-Paperman.mp3");
        bgMusic.setLooping(true);
        bgMusic.setVolume(0.5f);
        bgMusic.play();
    }

    @Override public void dispose() {
        uiStage.dispose();
    }

    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }

    public void beginCrazyMode() {
        ZOMBIE_SPAWN_INTERVAL = .5f;
        SUN_SPAWN_INTERVAL = 1.0f;
        gameManager.setCardCooldown(false);
    }
}
