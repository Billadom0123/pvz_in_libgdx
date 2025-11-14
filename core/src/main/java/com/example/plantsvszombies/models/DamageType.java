package com.example.plantsvszombies.models;

public enum DamageType {
    NORMAL(false, false, true),
    EXPLOSIVE(true, true, false),
    ICE(false, false, true),
    BOWLING(false, false, true),
    POTATO(false, false, false);

    private final boolean ignoreHelmet;
    private final boolean ignoreShield;
    private final boolean haveHitSound;

    DamageType(boolean ignoreHelmet, boolean ignoreShield, boolean haveHitSound) {
        this.ignoreHelmet = ignoreHelmet;
        this.ignoreShield = ignoreShield;
        this.haveHitSound = haveHitSound;
    }

    public boolean isIgnoreHelmet() {
        return ignoreHelmet;
    }

    public boolean isIgnoreShield() {
        return ignoreShield;
    }

    public boolean isHaveHitSound() {
        return haveHitSound;
    }
}
