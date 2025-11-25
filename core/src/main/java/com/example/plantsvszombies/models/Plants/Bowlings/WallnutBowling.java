package com.example.plantsvszombies.models.Plants.Bowlings;

import com.example.plantsvszombies.Resource.AssetAnimation;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.screens.BaseGameScreen;

public class WallnutBowling extends BaseBowling {


    public WallnutBowling(BaseGameScreen screen, float x, float y) {
        super(screen,
            AssetTexture.NORMAL_WALLNUT_STILL.get(AssetMan.getInstance()),
            AssetAnimation.WALLNUT.get(AssetMan.getInstance()),
              x, y);
        this.name = "Wallnut Bowling";
        this.health = 4000;
        this.speedX = 150f; // 设置保龄球的速度
        this.speedY = 150f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }
}
