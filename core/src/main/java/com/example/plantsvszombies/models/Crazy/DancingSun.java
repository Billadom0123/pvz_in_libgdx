package com.example.plantsvszombies.models.Crazy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.example.plantsvszombies.AssetImpl.AnimationClip;
import com.example.plantsvszombies.models.Sun;

public class DancingSun extends Sun {

    public DancingSun(int value, Texture texture, AnimationClip animation, float startX, float startY, float groundY) {
        super(value, texture, animation, startX, startY, groundY);
    }

    public static DancingSun generatedDancingSun(int value, Texture texture, AnimationClip animation, float startX, float startY, float groundY) {
        DancingSun s = new DancingSun(value, texture, animation, startX, startY, groundY);
        float endX = startX + MathUtils.random(200f, 400f);
        float endY = 0;
        float height = MathUtils.random(200f, 400f);
        s.startArcTo(endX, endY, height);
        // 可按需要调整时长
        s.arcDuration = MathUtils.random(2f, 3f);
        return s;
    }

    @Override
    public void update(float delta) {
        stateTime += delta;

        if (state == SunState.GENERATED) {
            arcTimer += delta;
            float t = Math.min(arcTimer / arcDuration, 1f);
            // 二次贝塞尔：B(t) = (1-t)^2*P0 + 2(1-t)t*C + t^2*P1
            float omt = 1f - t;
            float x = omt * omt * arcStart.x + 2f * omt * t * arcCtrl.x + t * t * arcEnd.x;
            float y = omt * omt * arcStart.y + 2f * omt * t * arcCtrl.y + t * t * arcEnd.y;

            setPosition(x, y);
            bounds.setPosition(x, y);

            if (t >= 1f) {
                state = SunState.ON_GROUND;
            }
            return;
        }

        if (state == SunState.FALLING) {
            float newY = getY() - fallSpeed * delta;
            if (newY <= groundY) {
                newY = groundY;
                state = SunState.ON_GROUND;
            }
            setPosition(getX(), newY); // 更新 Actor 的位置
            bounds.setPosition(getX(), getY()); // 同步边界位置
            return;
        }
        if (state == SunState.ON_GROUND) {
            arcDuration = .5f;
            startArcTo(getX() + 200f, getY() + 100f, 300f);
        }
    }
}
