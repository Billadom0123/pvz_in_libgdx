package com.example.plantsvszombies.ui;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.example.plantsvszombies.managers.GameManager;

public class PlantCard extends Actor {
    private final Texture cardTexture; // 卡片图片
    public final String plantType;     // 卡片代表的植物类型 (字符串标识)
    private final int cost;            // 种植所需太阳

    private boolean isSelected = false; // 是否被选中

    public PlantCard(Texture cardTexture, String plantType, int cost) {
        this.cardTexture = cardTexture;
        this.plantType = plantType;
        this.cost = cost;

        // 设置Actor的尺寸，这对于点击检测很重要
        setWidth(cardTexture.getWidth());
        setHeight(cardTexture.getHeight());

        // 添加点击监听器
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // 检查太阳是否足够
                if (GameManager.getInstance().getSun() >= cost) {
                    // 如果够，就“选中”这张卡片
                    ((PlantCard) event.getTarget()).select();
                    return true; // 返回true表示事件已被处理，不再向后传递
                }
                return false; // 太阳不够，点击无效
            }
        });
    }

    // --- 公共方法 ---
    public void select() {
        this.isSelected = true;
    }

    public void deselect() {
        this.isSelected = false;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public int getCost() {
        return cost;
    }

    /**
     * 重写draw方法，定义自己的绘制逻辑
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        // 继承父Actor的颜色和透明度
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        // 绘制卡片纹理
        batch.draw(cardTexture, getX(), getY(), getWidth(), getHeight());

        // 如果当前太阳数量不足以支付cost，则在卡片上覆盖一层半透明黑色，使其“变灰”
        if (GameManager.getInstance().getSun() < cost) {
            batch.setColor(0, 0, 0, 0.5f);
            batch.draw(cardTexture, getX(), getY(), getWidth(), getHeight());
            batch.setColor(color); // 恢复batch的颜色，以免影响后续绘制
        }

        // 如果被选中，可以绘制一个高亮效果（为简单起见，此处省略）
        if (isSelected) {
            // e.g., draw a highlight border
        }
    }
}
