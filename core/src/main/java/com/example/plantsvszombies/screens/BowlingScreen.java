package com.example.plantsvszombies.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.example.plantsvszombies.PvzGame;
import com.example.plantsvszombies.Resource.*;
import com.example.plantsvszombies.managers.*;
import com.example.plantsvszombies.models.DamageType;
import com.example.plantsvszombies.models.GameDialog;
import com.example.plantsvszombies.models.LawnMower;
import com.example.plantsvszombies.models.Plants.*;
import com.example.plantsvszombies.models.Plants.Bowlings.*;
import com.example.plantsvszombies.models.Projectiles.BaseProjectile;
import com.example.plantsvszombies.models.Sun;
import com.example.plantsvszombies.models.Texture.Falling;
import com.example.plantsvszombies.models.Zombies.*;
import com.example.plantsvszombies.ui.*;

import java.util.Arrays;
import java.util.Iterator;

public class BowlingScreen implements BaseGameScreen {

    // --- 核心引用 ---
    private final PvzGame game; // 对主游戏类的引用，用于访问全局对象如SpriteBatch
    private final AssetMan assetManager;
    private final GameManager gameManager;
    private final SoundManager soundManager;

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
    private final Array<LawnMower> lawnMowers;
    private final Array<Falling> fallings;
    private final Array<ParticleEffect> particles;
    private final Array<GameDialog> gameDialogs;

    // --- 游戏逻辑 ---
    private float gameTime = 0f;          // 游戏进行的总时间
    private final Rectangle[][] gridCells; // 逻辑网格，用于定位和种植
    private float zombieSpawnTimer;      // 僵尸生成计时器
    private float ZOMBIE_SPAWN_INTERVAL = 8.0f; // 默认僵尸生成间隔
    private boolean firstZombieSpawned = false;
    private boolean firstWaveSpawned = false;
    private boolean isCameraMoving = false; // 标记相机是否正在移动

    // --- UI 相关 ---
    private final Skin uiSkin;
    private final Stage uiStage;      // UI专属的舞台
    private PlantCard selectedCard; // 当前被玩家选中的植物卡片
    private Table tooltipTable;    // 显示卡片信息的提示框
    private Shovel shovel;;          // 铲子UI元素
    private final Texture stripe;
    private Conveyor conveyor;
    private PvzGraveButton pauseButton; // 暂停按钮
    private Texture whitePixel;
    private final com.badlogic.gdx.utils.Array<Group> modalStack = new com.badlogic.gdx.utils.Array<>();

    private float endBlackOverlayAlpha = 0f; // 黑色覆盖层的透明度
    private float endWordScale = 0f; // 抖动计时器

    private final float CONVEYOR_CARD_INTERVAL = 5.0f; // 传送带卡片生成间隔
    private float conveyorCardTimer = 0f; // 传送带卡片生成计时器

    public BowlingScreen(PvzGame game) {
        this.game = game;
        this.assetManager = AssetMan.getInstance();
        this.gameManager = GameManager.getInstance();
        this.soundManager = SoundManager.getInstance();

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
        lawnMowers = new Array<>();
        fallings = new Array<>();
        particles = new Array<>();
        gameDialogs = new Array<>();

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

        setupUI();

        stripe = AssetTexture.WALLNUT_BOWLING_STRIPE.get(assetManager);
        // 4. 初始化游戏网格及参数
        gridCells = new Rectangle[5][9];
        initializeGrid();
        initializeLawnMowers();
    }

    private void setupUI() {
        Table topBar = new Table(); // 使用Table来布局UI
        topBar.top().left(); // 对齐到舞台的左上角
        topBar.setPosition(cameraMinX, startY + 6 * cellHeight - 15);


        conveyor = new Conveyor(
            new TextureActor(AssetTexture.CONVEYOR_BELT_BACKDROP.get(assetManager)),
            new AnimationActor(AssetAnimation.CONVEYOR_BELT.get(assetManager)));
        conveyor.setPosition(startX + 15, startY + 5 * cellHeight - 15);
        conveyor.setSpeed(30f);

        shovel = new Shovel();
        shovel.setPosition(cameraMinX + 600, startY + 5 * cellHeight);

        pauseButton = new PvzGraveButton("Menu");
        pauseButton.setPosition(cameraMaxX - pauseButton.getWidth(), cameraMaxY - pauseButton.getHeight());
        pauseButton.setClickListener(this::pauseOnMenu);

        uiStage.addActor(topBar);
        uiStage.addActor(tooltipTable);
        uiStage.addActor(conveyor);
        uiStage.addActor(shovel);
        uiStage.addActor(pauseButton);
        tooltipTable.pack();
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
        game.batch.draw(AssetTexture.MAP_0.get(assetManager), 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        game.batch.draw(stripe, startX + 3 * cellWidth - 5, startY);


        game.batch.end();

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
//            Texture t = p.getTexture();
//            shapeRenderer.rect(p.getPosition().x, p.getPosition().y, t.getWidth(), t.getHeight());
//        }
//        shapeRenderer.end();

        uiStage.getViewport().apply();
        uiStage.draw();

        int screenX = Gdx.input.getX();
        int screenY = Gdx.input.getY();
        Vector2 worldCoords = gameViewport.unproject(new Vector2(screenX, screenY));

        game.batch.begin();

        for (BasePlant plant : plants) plant.drawShadow(game.batch);
        for (BaseZombie zombie : zombies) zombie.drawShadow(game.batch);
        for (BaseProjectile projectile : projectiles) projectile.drawShadow(game.batch);
        for (BasePlant plant : plants) plant.drawAnimation(game.batch);
        for (BaseZombie zombie : zombies) zombie.drawAnimation(game.batch);
        for (BaseProjectile projectile : projectiles) projectile.draw(game.batch);
        for (LawnMower lawnMower : lawnMowers) lawnMower.draw(game.batch);
        for (Falling falling : fallings) falling.draw(game.batch);
        for (ParticleEffect particleEffect : particles) { particleEffect.draw(game.batch); }


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

        game.batch.end();

        Batch batch = uiStage.getBatch();
        batch.setProjectionMatrix(uiStage.getCamera().combined);

        batch.begin();
        for (GameDialog dialog : gameDialogs) dialog.getDialogTable().draw(batch, 1f);
        if (gameManager.getGameState() == GameManager.GameState.PAUSED) {
            for (Group modal : modalStack) modal.draw(batch, 1f);
        }
        batch.end();

    }

    private boolean canPlantAt(int row, int col) {
        // 保龄球场景只允许在前3列种植
        if (row < 0 || row >= 5 || col < 0 || col >= 3) {
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

    @Override
    public void spawnProjectile(BaseProjectile projectile) {

    }

    @Override
    public void doAoeDamage(Rectangle area, int damage, DamageType type) {
        Array.ArrayIterator<BaseZombie> iterator = new Array.ArrayIterator<>(zombies);
        while (iterator.hasNext()) {
            BaseZombie z = iterator.next();
            if (area.overlaps(z.getCenterBounds())) {
                z.takeDamage(damage, type);
            }
        }
    }

    @Override
    public boolean isSunAvailable(int cost) {
        return false;
    }

    public void triggerCameraShake(float duration, float magnitude) {
        if (duration <= 0f || magnitude <= 0f) return;
        this.cameraShakeDuration = duration;
        this.cameraShakeTime = duration;
        this.cameraShakeMagnitude = magnitude;
        // 记录触发时的相机位置（支持在相机移动时触发）
        this.cameraShakeOrigin.set(gameCamera.position.x, gameCamera.position.y);
    }

    @Override
    public void addFalling(Falling falling) {
        fallings.add(falling);
    }

    @Override
    public void addParticle(ParticleEffect particle) {
        particles.add(particle);
    }

    @Override
    public void spawnGeneratedSun(int value, float x, float y) {

    }

    @Override
    public boolean anyZombieInHeight(float height) {
        return false;
    }

    @Override
    public void beginCrazyMode() {

    }

    @Override
    public void collectSun(Sun sun) {

    }

    @Override
    public int getRow(float y) {
        return MathUtils.floor((y - startY) / cellHeight);
    }

    @Override
    public int getCol(float x) {
        return MathUtils.floor((x - startX) / cellWidth);
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

    public void updateGame(float delta) {
        gameTime += delta;
        // 更新传送带
        conveyorCardTimer += delta;
        if (conveyorCardTimer >= CONVEYOR_CARD_INTERVAL) {
            conveyorCardTimer -= CONVEYOR_CARD_INTERVAL;
            if (conveyor.getCardCount() <= 10) {
                // 随机选择一种植物卡片生成
                float rand = MathUtils.random();
                if (rand < 0.7f) {
                    conveyor.addCard(new DisposablePlantCard(this, uiSkin, new WallnutBowling(this, 0, 0)));
                } else if (rand < 0.9f) {
                    conveyor.addCard(new DisposablePlantCard(this, uiSkin, new BoomBowling(this, 0, 0)));
                } else {
                    conveyor.addCard(new DisposablePlantCard(this, uiSkin, new HugeBowling(this, 0, 0)));
                }
            }
        }

        zombieSpawnTimer += delta;
        if (zombieSpawnTimer > ZOMBIE_SPAWN_INTERVAL) { // 每8秒生成一个
            int r = (int)gameTime / 100 + MathUtils.random(1, 4);
            for (int i = 0; i < r; i++) { spawnZombie(); }
            zombieSpawnTimer = 0;
        }

        for (BasePlant plant : plants) {
            plant.update(delta);
            if (plant.getPosition().x > cameraMaxX + 20f) {
                plant.dispose();
            }
        }
        for (BaseZombie zombie : zombies) zombie.update(delta);
        for (BaseProjectile projectile : projectiles) projectile.update(delta);
        for (LawnMower lawnMower : lawnMowers) lawnMower.update(delta);
        for (Falling falling : fallings) falling.update(delta);
        for (ParticleEffect particleEffect : particles) particleEffect.update(delta);

        checkCollisions();
        cleanupObjects();
        uiStage.act(delta);
    }

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
                    // 保龄球撞僵尸
                    if (p instanceof BaseBowling) {
                        ((BaseBowling) p).hit(z);
                    } else {
                    z.startAttacking(p); // 让僵尸进入攻击模式
                    isCurrentlyAttacking = true;
                    break; // 一个僵尸一次只攻击一个植物
                    }
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
    }





    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // --- 第一步：处理UI输入（舞台与游戏共用 viewport） ---
        Vector2 uiCoords = new Vector2(screenX, screenY);
        uiStage.getViewport().unproject(uiCoords);

        // 同步计算世界坐标，供种植判定
        Vector2 worldCoords = new Vector2(screenX, screenY);
        gameViewport.unproject(worldCoords);

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
                    if (cell.contains(worldCoords.x, worldCoords.y) && canPlantAt(cell) && col < 3) {
                        placePlant(cell);
                        return true; // 事件被游戏世界处理，结束
                    }
                }
            }
        }

        return false; // 点击了空白区域，事件未被处理
    }

    /**
     * 在指定的格子种植当前选中的植物
     * @param cell 目标格子
     */
    private void placePlant(Rectangle cell) {
        // 传送带模式，不花费阳光
        BasePlant newPlant = BasePlant.create(selectedCard.getPlantClass(), this, cell.x, cell.y);
        soundManager.playRandomSound(Arrays.asList(AssetSound.PLANT_1, AssetSound.PLANT_2));
        ParticleEffect plantEffect = AssetParticle.PLANTING.get(assetManager);
        plantEffect.setPosition(cell.x + cell.width / 2f, cell.y + 5);
        plantEffect.scaleEffect(.4f);
        plantEffect.start();
        particles.add(plantEffect);
        plants.add(newPlant); // 将新植物添加到世界中
        if (newPlant instanceof BaseBowling) {
            BaseBowling bowling = (BaseBowling) newPlant;
            bowling.beginRolling();
            if (gameManager.isCrazyMode()) {
                bowling.setRotationSpeed(bowling.getRotationSpeed() * 5f);
                bowling.setBounceInX(true);
            }
        }
        // 与之前不同，一次性卡片在种植后就不再保留
        conveyor.removeCard(selectedCard);

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

    private void spawnZombie() {
        int row = MathUtils.random(0, 4);
        float r = MathUtils.random();
        float xPosition = cameraMaxX + MathUtils.random(10f, 40f);
        float yPosition = gridCells[row][0].y;
        BaseZombie newZombie;
        if (r < 0.6f) {
            newZombie = new NormalZombie(this, xPosition, yPosition);
        } else if (r < 0.9f) {
            newZombie = new ConeHeadZombie(this, xPosition, yPosition);
        } else {
            newZombie = new BucketHeadZombie(this, xPosition, yPosition);
        }
        zombies.add(newZombie);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {

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
            tooltipTable.clear();
            Label nameLabel = new Label(card.getPlantType(), uiSkin);
            nameLabel.setColor(Color.BLACK);
            nameLabel.setFontScale(.6f);
            tooltipTable.add(nameLabel).row();

            tooltipTable.pack();

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
            if (tx < 0f) tx = 0f;
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
    public void show() {
        // 5. 将本类设置为输入处理器，这样touchDown等方法才会被调用
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage); // 先处理UI输入
        multiplexer.addProcessor(this);    // 再处理游戏输入
        Gdx.input.setInputProcessor(multiplexer);

        if (gameManager.isCrazyMode()) {
            soundManager.changeMusic(AssetMusic.HLSNW, true);
        } else {
            soundManager.changeMusic(AssetMusic.MINI_GAME, true);
        }
        if (gameManager.getGameState() == GameManager.GameState.PAUSED) soundManager.pauseMusic();
    }

    @Override public void resize(int width, int height) {
        // 禁止自动居中，避免把相机挪回 worldWidth/2 导致左偏
        gameViewport.update(width, height, false);
        // 手动把相机定位到用户提供的范围中心
        gameCamera.position.set((cameraMinX + cameraMaxX) / 2f, (cameraMinY + cameraMaxY) / 2f, 0f);
        gameCamera.update();
        // 同步 UI 的 viewport（与游戏共用对象，这里冗余但安全）
        uiStage.getViewport().update(width, height, false);
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
                return hit == null; // 拦截点击空白
            }
        });
        layer.addActor(bg);
        dialog.setPosition((w - dialog.getWidth()) / 2f, (h - dialog.getHeight()) / 2f);
        layer.addActor(dialog);
        uiStage.addActor(layer);
        modalStack.add(layer);
    }

    // 隐藏模态对话框
    @Override
    public void hideModal() {
        if (modalStack.size == 0) return;
        Group top = modalStack.pop();
        top.remove();
    }
    private void hideAllModals() {
        for (Group g : modalStack) g.remove();
        modalStack.clear();
    }
    @Override
    public void restart() {
        hideAllModals();
        gameManager.reset();
        game.setScreen(new BowlingScreen(game));
    }

    @Override
    public void pause() {
        if (gameManager.getGameState() == GameManager.GameState.PAUSED ||
            gameManager.getGameState() == GameManager.GameState.GAME_OVER ||
            gameManager.getGameState() == GameManager.GameState.STOPPED) {
            return; // 已经暂停或游戏结束，忽略
        }
        gameManager.setGameState(GameManager.GameState.PAUSED);

        soundManager.pauseSoundsAndMusic();
    }

    private void pauseOnDialog() {
        this.pause();
        PvzGraveButton resumeButton = new PvzGraveButton("Resume");
        PvzDialogBox pauseDialog = new PvzDialogBox("Game Paused", "Click to resume game", new PvzGraveButton[] {resumeButton});
        resumeButton.setClickListener(this::resume);
        showModal(pauseDialog, 0f);
        soundManager.playSound(AssetSound.PAUSE);
    }

    private void pauseOnMenu() {
        this.pause();
        PvzMenuPane menu = new PvzMenuPane(game, this);
        showModal(menu, 0f);
        soundManager.playSound(AssetSound.PAUSE);
    }

    @Override
    public void resume() {
        if (gameManager.getGameState() != GameManager.GameState.PAUSED) {
            return; // 不是暂停状态，忽略
        }
        gameManager.setGameState(GameManager.GameState.PLAYING);
        hideModal();
        soundManager.resumeSoundsAndMusic();
    }

    @Override public void hide() {
        // 清除输入处理器，避免内存泄漏
        if (Gdx.input.getInputProcessor() != null) {
            Gdx.input.setInputProcessor(null);
        }
    }
    @Override public void dispose() {}


    @Override public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE) {
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
        }
        return false;
    }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) {
        return false;
    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (Gdx.app.getType() == Application.ApplicationType.Android ||
            Gdx.app.getType() == Application.ApplicationType.iOS) {
            touchDown(screenX, screenY, pointer, button);
            if (selectedCard != null) {
                selectedCard.deselect();
                selectedCard = null;
            }
            if (shovel.isSelected()) {
                shovel.deselect();
            }
            return true;
        }
        return false;
    }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        mouseMoved(screenX, screenY);
        return false;
    }

    @Override public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void addGameDialog(String message, float duration) {

    }
}
