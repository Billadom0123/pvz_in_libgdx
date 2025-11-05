package com.example.plantsvszombies.managers;


public class GameManager {
    // 静态的唯一实例
    private static GameManager instance;

    // 游戏状态枚举
    public enum GameState {
        PLAYING, PAUSED, GAME_OVER, STOPPED
    }

    private static final int DEFAULT_SUN = 100;
    private boolean autoCollect = true;
    private boolean cardCooldown = true;
    private int sun; // 当前太阳数量
    private GameState gameState;

    private GameManager() {
        resetToDefaults();
    }

    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    // --- 重置相关 ---
    private void resetToDefaults() {
        this.autoCollect = false;
        this.cardCooldown = true;
        this.sun = DEFAULT_SUN;
        this.gameState = GameState.PLAYING;
    }

    /**
     * 重置为默认新局状态
     */
    public void reset() {
        resetToDefaults();
    }

    /**
     * 带自定义初始值的重置
     */
    public void reset(int initialSun, boolean autoCollect, boolean cardCooldown, GameState state) {
        this.sun = initialSun;
        this.autoCollect = autoCollect;
        this.cardCooldown = cardCooldown;
        this.gameState = state;
    }

    /**
     * 静态便捷方法，开始新局（调用现有单例的 reset）
     */
    public static void startNewGame() {
        getInstance().reset();
    }

    // --- 公共接口 ---


    public boolean isCardCooldownOn() {
        return cardCooldown;
    }

    public void setCardCooldown(boolean cardCooldown) {
        this.cardCooldown = cardCooldown;
    }

    public boolean isAutoCollect() { return autoCollect; }

    public void setAutoCollect(boolean autoCollect) { this.autoCollect = autoCollect; }

    public int getSun() {
        return sun;
    }

    public void addSun(int amount) {
        this.sun += amount;
    }

    /**
     * 尝试花费太阳。如果足够，则扣除并返回true；否则返回false。
     * @param amount 要花费的数量
     * @return 是否花费成功
     */
    public boolean spendSun(int amount) {
        if (sun >= amount) {
            sun -= amount;
            return true;
        }
        return false;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
