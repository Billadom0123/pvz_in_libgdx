package com.example.plantsvszombies.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.example.plantsvszombies.Resource.Almanac.AlmanacZombie;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.models.Zombies.BaseZombie;

public class AlmanacZombieCard extends Group {
    protected final AssetMan assetMan;
    protected final AlmanacZombie almanacZombie;
    protected final TextureActor border;
    protected final TextureActor background;
    protected final Class<? extends BaseZombie> zombieClass;
    protected final TextureActor zombieTexture;
    protected final float zombieScale;
    protected final float zombieXCenterPercent;
    protected final float zombieYCenterPercent;

    public AlmanacZombieCard(AlmanacZombie zombie) {
        this.assetMan = AssetMan.getInstance();
        this.almanacZombie = zombie;
        this.border = new TextureActor(AssetTexture.ALMANAC_ZOMBIE_WINDOW_BORDER.get(assetMan));
        this.background = new TextureActor(AssetTexture.ALMANAC_ZOMBIE_WINDOW_BG.get(assetMan));
        BaseZombie baseZombie = BaseZombie.create(zombie.getZombieClass(), null, 0, 0);
        if (baseZombie == null) {
            Gdx.app.error("AlmanacZombieCard", "Failed to create BaseZombie for AlmanacZombieCard.");
        }
        this.zombieClass = zombie.getZombieClass();
        this.zombieTexture = new TextureActor(baseZombie.getTexture());
        this.zombieScale = zombie.getScale();
        this.zombieXCenterPercent = zombie.getXCenterPercent();
        this.zombieYCenterPercent = zombie.getYCenterPercent();
        layout();
        this.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // 可以添加悬停效果
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                // 可以移除悬停效果
            }
        });
    }

    private void layout() {
        setSize(border.getWidth(), border.getHeight());
        // 顺序：先背景，再僵尸，最后边框
        addActor(background);
        // 根据背景大小居中放 zombie（你可以把 scale 改回 1，看清裁剪效果）
        zombieTexture.setScale(zombieScale);
        float zx = (background.getWidth() - zombieTexture.getWidth() * zombieScale * 2 * zombieXCenterPercent) / 2f;
        float zy = (background.getHeight() - zombieTexture.getHeight() * zombieScale * 2 * zombieYCenterPercent) / 2f;
        zombieTexture.setPosition(zx, zy);
        zombieTexture.setTouchable(Touchable.disabled);
        addActor(zombieTexture);
        addActor(border);
    }

    @Override
    protected void drawChildren(Batch batch, float parentAlpha) {
        if (clipBegin(5, 5, getWidth() - 5, getHeight() - 5)) {
            super.drawChildren(batch, parentAlpha);
            clipEnd();
        } else {
//            Gdx.app.error("AlmanacZombieCard", "Failed to set up clipping for AlmanacZombieCard.");
            // Todo: 研究出来为什么会失败
            super.drawChildren(batch, parentAlpha);
        }
    }

    public Class<? extends BaseZombie> getZombieClass() { return zombieClass; }

    public AlmanacZombie getAlmanacZombie() { return almanacZombie; }
}
