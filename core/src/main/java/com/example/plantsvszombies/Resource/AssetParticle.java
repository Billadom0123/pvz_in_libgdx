package com.example.plantsvszombies.Resource;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.example.plantsvszombies.managers.AssetMan;

public enum AssetParticle {

    PLANTING("graphics/Particles/Planting.p", "graphics/Particles/dirtsmall"),
    POTATO_MINE_RISE("graphics/Particles/PotatoMine_Up.p", "graphics/Particles/dirtsmall"),
    POTATO_MINE_EXPLODE("graphics/Particles/PotatoMine_Explode.p", "graphics/Particles/potatomine"),
    SEED_PACKET_FLASH("graphics/Particles/SeedPacketFlash.p", "graphics/Particles/seedpacket");


    private final String filePath;
    private final String ImageDirectory;

    AssetParticle(String filePath, String imageDirectory) {
        this.filePath = filePath;
        this.ImageDirectory = imageDirectory;
    }

    public void load(AssetMan assetMan) {
        assetMan.loadParticleEffect(this.name(), this.filePath, this.ImageDirectory);
    }

    public static void loadAll(AssetMan assetMan) {
        for (AssetParticle particle : AssetParticle.values()) {
            particle.load(assetMan);
        }
    }

    public ParticleEffect get(AssetMan assetMan) {
        return assetMan.getParticleEffect(this.name());
    }
}
