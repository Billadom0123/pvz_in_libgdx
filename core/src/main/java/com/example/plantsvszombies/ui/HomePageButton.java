package com.example.plantsvszombies.ui;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.example.plantsvszombies.managers.SoundManager;

public class HomePageButton extends PressableGroup {
    private Texture texture; // 当前用于绘制的纹理
    private final Texture baseTexture; // 原始纹理，用于恢复
    private final Texture highlightTexture;
    private final Texture shadowTexture;
    private final Shape2D hitArea;
    private boolean locked = false;

    public HomePageButton(Texture texture, Texture highlightTexture, Texture shadowTexture,
                          Sound hoverSound, Sound clickSound, Shape2D hitArea) {
        this.baseTexture = texture;
        this.texture = baseTexture;
        this.highlightTexture = highlightTexture;
        this.shadowTexture = shadowTexture;
        this.hitArea = hitArea;
        if (hitArea != null) {
            if (hitArea instanceof Polygon) {
                ((Polygon)this.hitArea).setPosition(getX(), getY());
            }
        }
        setSize(texture.getWidth(), texture.getHeight());

        // 添加悬停事件监听器：使用 enter/exit，enter 只会在 hit(...) 判定为非 null 时触发
        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // 再次确认是在 hitArea 内（冗余校验）
                if (hit(x, y, true) != null) {
                    HomePageButton.this.texture = HomePageButton.this.highlightTexture;
                    if (hoverSound != null) {
                        SoundManager.getInstance().playSound(hoverSound);
                    }
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                // 鼠标移出时恢复原始纹理
                HomePageButton.this.texture = HomePageButton.this.baseTexture;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (clickSound != null)  SoundManager.getInstance().playSound(clickSound);
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
        if (isLocked() && shadowTexture != null) {
            batch.draw(shadowTexture, getX(), getY(), getWidth(), getHeight());
        }
        batch.end();

//        // 使用类成员变量的 ShapeRenderer
//        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(1, 0, 0, 1); // 设置颜色为红色
//        if (hitArea instanceof Polygon) {
//            shapeRenderer.polygon(((Polygon) hitArea).getTransformedVertices());
//        } else if (hitArea instanceof Rectangle) {
//            Rectangle rect = (Rectangle) hitArea;
//            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
//        }
//        shapeRenderer.end();

        batch.begin();
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && getTouchable() == Touchable.disabled) {
            return null;
        }


        if (hitArea != null) {
            // 将 actor 局部坐标转换为世界坐标再检测 hitArea
            float worldX = x + getX();
            float worldY = y + getY();

            if (hitArea.contains(worldX, worldY)) {
                return this;
            }
        }
        return null;
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        if (hitArea != null) {
            if (hitArea instanceof Polygon) {
                ((Polygon)this.hitArea).setPosition(x, y);
            }
        }
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        if (locked) {
            setTouchable(Touchable.disabled);
        } else {
            setTouchable(Touchable.enabled);
        }
    }

    public boolean isLocked() {
        return locked;
    }
}
