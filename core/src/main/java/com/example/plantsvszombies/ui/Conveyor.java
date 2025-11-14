// java
package com.example.plantsvszombies.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Conveyor extends Group {
    private TextureActor background;
    private AnimationActor track;
    private Group cardLayer;              // 自定义裁剪层
    private Array<PlantCard> cards;
    private boolean moving = true;
    private float speed = 15f;

    public Conveyor(TextureActor background, AnimationActor track) {
        this.background = background;
        this.track = track;

        background.setPosition(0, 0);
        track.setPosition(7, 7);

        // 绘制顺序：背景 -> 履带 -> 卡片层（裁剪）
        addActor(this.background);
        addActor(this.track);

        cardLayer = new CardClipGroup();
        cardLayer.setPosition(background.getX(), background.getY());
        cardLayer.setSize(background.getWidth() - 7, background.getHeight());
        cardLayer.setTransform(false);
        addActor(cardLayer);
    }

    // 绑定卡片列表
    public void bindCards(Array<PlantCard> cards) {
        this.cards = cards;
        updateUI();
    }

    // 只刷新卡片层
    private void updateUI() {
        if (cardLayer == null) return;
        cardLayer.clearChildren();
        if (cards != null) {
            for (PlantCard card : cards) {
                cardLayer.addActor(card);
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!moving || cards == null || cards.size == 0) return;

        float wantedDeltaX = -speed * delta;

        for (int i = 0; i < cards.size; i++) {
            PlantCard card = cards.get(i);

            float minX;
            if (i == 0) {
                minX = 3f; // 左内边距（cardLayer 局部坐标）
            } else {
                PlantCard left = cards.get(i - 1);
                minX = left.getX() + left.getWidth();
            }
            float stopDistance = 2f;
            float minAllowedXWithDistance = minX + stopDistance;

            float potentialX = card.getX() + wantedDeltaX;
            float moveBy = wantedDeltaX;

            if (potentialX < minAllowedXWithDistance) {
                moveBy = minAllowedXWithDistance - card.getX();
            }
            if (moveBy != 0f) {
                card.moveBy(moveBy, 0f);
            }
        }
    }

    public void addCard(PlantCard card) {
        if (cards == null) cards = new Array<>();
        cards.add(card);

        // 初始位置：X 在可见区域右侧；Y 与履带对齐（转换到 cardLayer 局部坐标）
        float initX = cardLayer.getWidth();
        float initY = track.getY() + 1f - cardLayer.getY();
        card.setPosition(initX, initY);

        cardLayer.addActor(card);
    }

    public boolean removeCard(PlantCard card) {
        if (cards == null) return false;
        boolean removed = cards.removeValue(card, true);
        if (removed) {
            card.remove();
        }
        return removed;
    }

    public void clearCards() {
        if (cards == null) return;
        for (PlantCard c : cards) c.remove();
        cards.clear();
        cardLayer.clearChildren();
    }

    public void dispose() {
        if (cards == null) return;
        for (PlantCard card : cards) {
            card.remove();
            card.clearActions();
            if (card instanceof Disposable) {
                try { ((Disposable) card).dispose(); } catch (Exception ignored) {}
            }
        }
        cards.clear();
        cardLayer.clearChildren();
    }

    public float getSpeed() {
        return speed;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getCardCount() { return cards == null ? 0 : cards.size; }

    // 仅裁剪子节点的自定义分组，兼容无 WidgetGroup.setClip 的 gdx 版本
    private static class CardClipGroup extends Group {
        @Override
        protected void drawChildren(Batch batch, float parentAlpha) {
            if (clipBegin(0, 0, getWidth(), getHeight())) {
                super.drawChildren(batch, parentAlpha);
                clipEnd();
            } else {
                super.drawChildren(batch, parentAlpha);
            }
        }
        @Override
        public Actor hit(float x, float y, boolean touchable) {
            // 裁剪区域外不响应点击
            if (x < 0 || y < 0 || x > getWidth() || y > getHeight()) return null;
            return super.hit(x, y, touchable);
        }
    }
}
