package com.example.plantsvszombies.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

// java
public class PressableGroup extends Group {
    private float offsetX = 1f;
    private float offsetY = -2f;
    private float originalX, originalY;
    private boolean pressed;

    public interface CustomClickListener {
        void clicked();
    }
    private CustomClickListener customClickListener;

    public PressableGroup() {
        addInput();
    }

    public PressableGroup(float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        addInput();
    }

    public void setClickListener(CustomClickListener l) {
        this.customClickListener = l;
    }

    private void addInput() {
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                originalX = getX();
                originalY = getY();
                setPosition(originalX + offsetX, originalY + offsetY);
                pressed = true;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                // 若拖出范围则取消视觉按下
                if (x < 0 || y < 0 || x > getWidth() || y > getHeight()) {
                    if (pressed) {
                        setPosition(originalX, originalY);
                        pressed = false;
                    }
                } else if (!pressed) {
                    // 回到范围内再显示按下
                    setPosition(originalX + offsetX, originalY + offsetY);
                    pressed = true;
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                setPosition(originalX, originalY);
                if (pressed && x >= 0 && y >= 0 && x <= getWidth() && y <= getHeight()) {
                    if (customClickListener != null) customClickListener.clicked();
                }
                pressed = false;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                // 鼠标移出时如果仍按住可复位
                if (pressed) {
                    setPosition(originalX, originalY);
                    pressed = false;
                }
            }
        });
    }
}
