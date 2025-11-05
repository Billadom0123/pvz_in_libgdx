package com.example.plantsvszombies.models.Projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.example.plantsvszombies.Resource.AssetAnimation;
import com.example.plantsvszombies.Resource.AssetSound;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.models.Zombies.BaseZombie;
import com.example.plantsvszombies.models.Zombies.BucketHeadZombie;
import com.example.plantsvszombies.models.Zombies.ConeHeadZombie;
import com.example.plantsvszombies.models.Zombies.NormalZombie;

import java.util.List;

public class Pea extends BaseProjectile {
    public Pea(Texture texture, float x, float y) {
        super(texture, 20, x, y);
        setHitAnimation(AssetAnimation.PEA_SPLAT.get(AssetMan.getInstance()));
    }

    @Override
    public void damage(BaseZombie zombie) {
        if (hit) {
            return;
        }
        AssetMan assetMan = AssetMan.getInstance();
        if (zombie instanceof ConeHeadZombie) {
            if (!((ConeHeadZombie)zombie).isConeGone()) {
                assetMan.playSound(AssetSound.PEA_SPLAT_CONEHEAD, .5f);
            } else {
                assetMan.playSound(AssetSound.PEA_SPLAT, .5f);
            }
        } else if (zombie instanceof BucketHeadZombie) {
            if (!((BucketHeadZombie)zombie).isBucketGone()) {
                assetMan.playRandomSound(List.of(AssetSound.PEA_SPLAT_BUCKETHEAD_1, AssetSound.PEA_SPLAT_BUCKETHEAD_2), .5f);
            } else {
                assetMan.playSound(AssetSound.PEA_SPLAT, .5f);
            }
        } else if (zombie instanceof NormalZombie) {
            assetMan.playSound(AssetSound.PEA_SPLAT, .5f);
        }
        super.damage(zombie);
    }
}
