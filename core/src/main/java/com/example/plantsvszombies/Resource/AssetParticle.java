package com.example.plantsvszombies.Resource;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.example.plantsvszombies.managers.AssetMan;

public enum AssetParticle {

    PLANTING("graphics/Particles/Planting.p", "graphics/Particles/dirtsmall"),
    POTATO_MINE_RISE("graphics/Particles/PotatoMine_Up.p", "graphics/Particles/dirtsmall"),
    POTATO_MINE_EXPLODE("graphics/Particles/PotatoMine_Explode.p", "graphics/Particles/potatomine"),
    SEED_PACKET_FLASH("graphics/Particles/SeedPacketFlash.p", "graphics/Particles/seedpacket"),
    SNOW_PEA("graphics/Particles/SnowPea.p", "graphics/Particles/Snowflakes");


    private final String filePath;
    private final String ImageDirectory;
    private boolean loaded;

    AssetParticle(String filePath, String imageDirectory) {
        this.filePath = filePath;
        this.ImageDirectory = imageDirectory;
        this.loaded = false;
    }

    public void load(AssetMan assetMan) {
        if (loaded) { return; }
        assetMan.loadParticleEffect(this.name(), this.filePath, this.ImageDirectory);
        loaded = true;
    }

    public static void loadAll(AssetMan assetMan) {
        for (AssetParticle particle : AssetParticle.values()) {
            particle.load(assetMan);
        }
    }

    public ParticleEffect get(AssetMan assetMan) {
        if (!loaded) { load(assetMan); }
        return assetMan.getParticleEffect(this.name());
    }

    public ParticleEffect loadAndGet(AssetMan assetMan) {
        load(assetMan);
        assetMan.getAssetManager().finishLoading();
        return assetMan.getParticleEffect(this.name());
    }
}
