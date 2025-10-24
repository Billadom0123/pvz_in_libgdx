package com.example.plantsvszombies.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.example.plantsvszombies.Animation.AnimationClip;
import com.example.plantsvszombies.PvzGame;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.managers.GameManager;
import com.example.plantsvszombies.models.LawnMower;
import com.example.plantsvszombies.models.Plants.BasePlant;
import com.example.plantsvszombies.models.Plants.Peashooter;
import com.example.plantsvszombies.models.Plants.Sunflower;
import com.example.plantsvszombies.models.Plants.Wallnut;
import com.example.plantsvszombies.models.Projectiles.BaseProjectile;
import com.example.plantsvszombies.models.Sun;
import com.example.plantsvszombies.models.Zombies.BaseZombie;
import com.example.plantsvszombies.models.Zombies.NormalZombie;
import com.example.plantsvszombies.ui.PlantCard;

import java.util.Iterator;

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

    // --- 游戏对象列表 ---
    private final Array<BasePlant> plants;
    private final Array<BaseZombie> zombies;
    private final Array<BaseProjectile> projectiles;
    private final Array<Sun> suns;
    private final Array<LawnMower> lawnMowers;

    // --- 游戏逻辑 ---
    private final Rectangle[][] gridCells; // 逻辑网格，用于定位和种植
    private float zombieSpawnTimer;      // 僵尸生成计时器
    private float sunSpawnTimer;         // 太阳生成计时器

    // --- UI 相关 ---
    private final Stage uiStage;      // UI专属的舞台
    private final Label sunLabel;     // 显示太阳数量的标签
    private PlantCard selectedCard; // 当前被玩家选中的植物卡片

    /**
     * GameScreen的构造函数
     */
    public GameScreen(PvzGame game) {
        this.game = game;
        this.assetManager = AssetMan.getInstance();
        this.gameManager = GameManager.getInstance();

        // 1. 初始化游戏相机和视口
        gameCamera = new OrthographicCamera();
        gameViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, gameCamera);
        gameCamera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0); // 相机居中

        // 2. 初始化游戏对象列表
        plants = new Array<>();
        zombies = new Array<>();
        projectiles = new Array<>();
        suns = new Array<>();
        lawnMowers = new Array<>();

        // 3. 初始化UI
        uiStage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT)); // UI使用独立的舞台和视口
        sunLabel = setupUI(); // setupUI方法会创建UI元素并返回sunLabel的引用

        // 4. 初始化游戏网格
        gridCells = new Rectangle[5][9];
        initializeGrid();
        initializeLawnMowers();

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
        topBar.setFillParent(true); // 填满整个舞台
        topBar.top().left(); // 对齐到舞台的左上角

        // 创建植物卡片
        PlantCard peashooterCard = new PlantCard(assetManager.getTexture("graphics/Cards/card_peashooter.png"), "Peashooter", 100);
        PlantCard sunflowerCard = new PlantCard(assetManager.getTexture("graphics/Cards/card_sunflower.png"), "Sunflower", 50);
        PlantCard wallnutCard = new PlantCard(assetManager.getTexture("graphics/Cards/card_wallnut.png"), "WallNut", 50);

        // 将卡片添加到Table中
        topBar.add(peashooterCard).pad(10);
        topBar.add(sunflowerCard).pad(10);
        topBar.add(wallnutCard).pad(10);

        // 创建太阳标签
        Skin skin = new Skin(Gdx.files.internal("graphics/Skin/uiskin.json"));
        Label label = new Label("Sun: " + gameManager.getSun(), skin);
        topBar.add(label).padLeft(20);

        uiStage.addActor(topBar);
        return label;
    }

    /**
     * 初始化草坪的逻辑网格
     */
    private void initializeGrid() {
        float cellWidth = 80;   // 这些数值需要根据你的背景图精确测量
        float cellHeight = 98;
        float startX = 265;
        float startY = 40;

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
            lawnMowers.add(new LawnMower(lawnMowerTexture, -20, yPosition + 15));
        }
    }

    @Override
    public void render(float delta) {
        // 1. 清理屏幕
        Gdx.gl.glClearColor(0.3f, 0.7f, 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 2. 更新游戏逻辑 (仅在游戏进行时)
        if (gameManager.getGameState() == GameManager.GameState.PLAYING) {
            updateGame(delta);
        }

        // 3. 渲染游戏世界
        gameViewport.apply(); // 应用游戏视口
        gameCamera.update();
        game.batch.setProjectionMatrix(gameCamera.combined); // 设置投影矩阵

        game.batch.begin();
        // 绘制背景
        game.batch.draw(assetManager.getTexture("graphics/Map/map0.jpg"), 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        // 绘制所有游戏对象
        for (BasePlant plant : plants) plant.drawAnimation(game.batch);
        for (BaseZombie zombie : zombies) zombie.drawAnimation(game.batch);
        for (BaseProjectile projectile : projectiles) projectile.draw(game.batch);
        for (Sun sun : suns) sun.draw(game.batch);
        for (LawnMower lawnMower : lawnMowers) lawnMower.draw(game.batch);
        game.batch.end();

        // 4. 渲染UI
        sunLabel.setText("Sun: " + gameManager.getSun()); // 更新UI标签
        uiStage.getViewport().apply(); // 应用UI视口
        uiStage.act(delta); // 更新UI舞台（例如，执行Action）
        uiStage.draw();     // 绘制UI舞台
    }

    /**
     * 封装了所有游戏世界的更新逻辑
     */
    private void updateGame(float delta) {
        // 更新所有游戏对象
        for (BasePlant plant : plants) plant.update(delta);
        for (BaseZombie zombie : zombies) zombie.update(delta);
        for (BaseProjectile projectile : projectiles) projectile.update(delta);

        // 计时生成僵尸
        zombieSpawnTimer += delta;
        if (zombieSpawnTimer > 8.0f) { // 每8秒生成一个
            spawnZombie();
            zombieSpawnTimer = 0;
        }

        sunSpawnTimer += delta;
        if (sunSpawnTimer > 12.0f) { // 每12秒生成
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
        float randomX = com.badlogic.gdx.math.MathUtils.random(50f, WORLD_WIDTH - 50f);
        float randomGroundY = com.badlogic.gdx.math.MathUtils.random(50f, WORLD_HEIGHT - 200f);
        suns.add(new Sun(assetManager.getTexture("graphics/Screen/Sun.gif"), randomX, WORLD_HEIGHT, randomGroundY));
    }

    /**
     * 在随机一行生成一个僵尸
     */
    private void spawnZombie() {
        int row = com.badlogic.gdx.math.MathUtils.random(0, 4);
        // 在屏幕右侧外生成
        Texture zombieTexture = assetManager.getTexture("graphics/Zombies/Zombie/Zombie.gif");
        AnimationClip zombieAnimation = assetManager.getAnimation("NormalZombie");
        zombies.add(new NormalZombie(zombieTexture, zombieAnimation, WORLD_WIDTH, gridCells[row][0].y));
    }

    /**
     * 检查游戏结束条件
     */
    private void checkGameOver() {
        for(BaseZombie z : zombies) {
            if(z.getX() < 0) { // 如果有僵尸走到了屏幕最左边
                gameManager.setGameState(GameManager.GameState.GAME_OVER);
                System.out.println("GAME OVER");
                // 在实际游戏中，这里应该切换到游戏结束画面
                // game.setScreen(new GameOverScreen(game));
            }
        }
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
                    z.takeDamage(p.getDamage()); // 僵尸掉血
                    p.active = false;   // 子弹失效
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
                        z.alive = false; // 僵尸被大运撞死
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
            if (!it.next().alive) {
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
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // --- 第一步：处理UI输入 ---
        Vector3 uiCoords = new Vector3(screenX, screenY, 0);
        uiStage.getViewport().unproject(uiCoords); // 将屏幕坐标转换为UI舞台坐标

        for (Iterator<Sun> it = suns.iterator(); it.hasNext();) {
            Sun sun = it.next();
            if (sun.getBounds().contains(uiCoords.x, uiCoords.y)) {
                sun.collect();
                gameManager.addSun(25);
                it.remove();
                return true; // 事件被UI处理，结束
            }
        }

        Actor hitActor = uiStage.hit(uiCoords.x, uiCoords.y, true); // 检测是否点中UI演员
        if (hitActor instanceof PlantCard) {
            // 如果点中的是植物卡片
            PlantCard card = (PlantCard) hitActor;
            if (card.isSelected()) { // 如果卡片已经被选中，则取消选中
                card.deselect();
                selectedCard = null;
            } else {
                if (selectedCard != null) selectedCard.deselect(); // 取消之前选中的卡片
                selectedCard = card; // 选中新卡片
            }
            return true; // 事件被UI处理，结束
        }

        // --- 第二步：如果UI未处理，则处理游戏世界输入 ---
        if (selectedCard != null) {
            // 如果当前有选中的卡片，说明玩家想种植
            Vector3 worldPos = new Vector3(screenX, screenY, 0);
            gameViewport.getCamera().unproject(worldPos); // 将屏幕坐标转换为游戏世界坐标

            // 遍历所有网格，检查点击位置
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 9; col++) {
                    Rectangle cell = gridCells[row][col];
                    if (cell.contains(worldPos.x, worldPos.y)) {
                        // 在这个格子上种植植物
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
        // 尝试花费太阳
        if (gameManager.spendSun(selectedCard.getCost())) {
            Texture plantTexture;
            AnimationClip plantAnimation;
            BasePlant newPlant;
            // 根据选中的卡片类型，创建不同的植物实例
            if (selectedCard.plantType.equals("Peashooter")) {
                plantTexture = assetManager.getTexture("graphics/Plants/Peashooter/0.gif");
                plantAnimation = assetManager.getAnimation("Peashooter");
                newPlant = new Peashooter(this, plantTexture, plantAnimation, cell.x, cell.y);
            } else if (selectedCard.plantType.equals("Sunflower")) {
                plantTexture = assetManager.getTexture("graphics/Plants/SunFlower/0.gif");
                plantAnimation = assetManager.getAnimation("SunFlower");
                newPlant = new Sunflower(plantTexture, plantAnimation, cell.x, cell.y);
            } else if (selectedCard.plantType.equals("WallNut")) {
                plantTexture = assetManager.getTexture("graphics/Plants/WallNut/0.gif");
                plantAnimation = assetManager.getAnimation("WallNut");
                newPlant = new Wallnut(plantTexture, plantAnimation, cell.x, cell.y); // 这里应该是WallNut类
            } else {
                return; // 未知的植物类型
            }
            plants.add(newPlant); // 将新植物添加到世界中
        }

        // 无论种植成功与否，都取消卡片的选择状态
        selectedCard.deselect();
        selectedCard = null;
    }

    // --- 其他 Screen 和 InputProcessor 的方法 ---

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);
        uiStage.getViewport().update(width, height, true);
    }

    // 让GameScreen能够生成子弹 (被Peashooter调用)
    public void spawnProjectile(BaseProjectile projectile) {
        projectiles.add(projectile);
    }

    // 让GameScreen能够获取AssetManager (被Peashooter调用)
    public AssetManager getAssetManager() {
        return assetManager.getAssetManager();
    }

    @Override public void show() { }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() { uiStage.dispose(); }
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}
