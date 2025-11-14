package com.example.plantsvszombies.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.example.plantsvszombies.models.Plants.BasePlant;
import com.example.plantsvszombies.screens.BaseGameScreen;

public class DisposablePlantCard extends PlantCard {

    public DisposablePlantCard(BaseGameScreen screen, Skin skin, BasePlant plant) {
        super(screen, skin, plant);
    }

    @Override
    public void layout() {
        super.layout();
        this.costLabel.setVisible(false);
    }

    @Override public boolean isOnCooldown() { return false; } // 永远不在冷却
    @Override public boolean isSunEnough() { return true; } // 永远足够阳光
}
