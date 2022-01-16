package fr.flowsqy.abstractmob.entity;

import org.bukkit.entity.EntityType;

import java.util.Objects;

public class CustomEntity {

    private EntityType type;
    private int quantity;

    private double maxLife;

    private int lightningChances;
    private int spiderWebChances;
    private double knockBack;

    public CustomEntity(EntityType type, int quantity) {
        setType(type);
        setQuantity(quantity);
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        Objects.requireNonNull(type);
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Can not set quantity to 0 or bellow");
        }
        this.quantity = quantity;
    }

    public double getMaxLife() {
        return maxLife;
    }

    public void setMaxLife(double maxLife) {
        // TODO Check this with the modifier for max life
        if (maxLife <= 0.5) {
            throw new IllegalArgumentException("Can not set quantity to 0.5 or bellow");
        }
        this.maxLife = maxLife;
    }

    public int getLightningChances() {
        return lightningChances;
    }

    public void setLightningChances(int lightningChances) {
        this.lightningChances = lightningChances;
    }

    public int getSpiderWebChances() {
        return spiderWebChances;
    }

    public void setSpiderWebChances(int spiderWebChances) {
        this.spiderWebChances = spiderWebChances;
    }

    public double getKnockBack() {
        return knockBack;
    }

    public void setKnockBack(double knockBack) {
        this.knockBack = knockBack;
    }
}
