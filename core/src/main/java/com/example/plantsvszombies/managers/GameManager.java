package com.example.plantsvszombies.managers;


public class GameManager {
    // 静态的唯一实例
    private static GameManager instance;

    // 游戏状态枚举
    public enum GameState {
        PLAYING, // 正在进行
        PAUSED,  // 暂停
        GAME_OVER // 游戏结束
    }

    private int sun; // 当前太阳数量
    private GameState gameState;

    // 私有构造函数，防止外部直接创建实例
    private GameManager() {
        sun = 100; // 游戏开始时给予100个太阳
        gameState = GameState.PLAYING;
    }

    /**
     * 获取单例的全局访问点
     * @return GameManager的唯一实例
     */
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    // --- 公共接口 ---

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
