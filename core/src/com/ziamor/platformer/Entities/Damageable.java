package com.ziamor.platformer.Entities;

public interface Damageable {
    float getCurrentHealth();

    float getMaxHealth();

    boolean isDead();

    void applyDamage(float dmg_value);

    void applyHeal(float heal_value);
}
