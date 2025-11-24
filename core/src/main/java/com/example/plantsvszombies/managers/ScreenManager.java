package com.example.plantsvszombies.managers;

import com.badlogic.gdx.Screen;
import com.example.plantsvszombies.PvzGame;
import com.example.plantsvszombies.screens.AlmanacScreen;
import com.example.plantsvszombies.screens.BaseGameScreen;
import com.example.plantsvszombies.screens.HomeScreen;


public class ScreenManager {

    private static ScreenManager instance;

    private PvzGame game;

    private static final String TAG = "ScreenManager";

    private HomeScreen homeScreen;

    private AlmanacScreen almanacScreen;

    private BaseGameScreen gameScreen;

    private ScreenManager() {
    }


    public static synchronized ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    public void initialize(PvzGame game) {
        this.game = game;
    }

    public void setScreen(Screen screen) {
        game.setScreen(screen);
    }

}
