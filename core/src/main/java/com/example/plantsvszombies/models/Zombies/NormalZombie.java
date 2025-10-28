package com.example.plantsvszombies.models.Zombies;


import com.badlogic.gdx.graphics.Texture;
import com.example.plantsvszombies.Animation.AnimationClip;
import com.example.plantsvszombies.screens.GameScreen;

public class NormalZombie extends BaseZombie {
    public NormalZombie(Texture texture, AnimationClip animation, float x, float y) {
        super(texture, animation, x, y);
    }

    public NormalZombie(GameScreen gameScreen, Texture texture, AnimationClip animation, float x, float y) {
        super(gameScreen, texture, animation, x, y);
    }
}
