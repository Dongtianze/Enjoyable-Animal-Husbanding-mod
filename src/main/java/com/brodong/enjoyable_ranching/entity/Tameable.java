package com.brodong.enjoyable_ranching.entity;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public interface Tameable {

    /** 驯化概率分母：每喂食一次有 1/TAMING_CHANCE 概率驯化 */
    int TAMING_CHANCE = 4;

    boolean isTamed();
    void setTamed(boolean tamed);
    UUID getOwnerUUID();
    void setOwnerUUID(UUID owner);
    void tame(Player player);

    static boolean isTameable(Animal animal) {
        return animal instanceof Cow || animal instanceof Sheep || animal instanceof Pig;
    }

    /** 牛：是否可装备背包（已驯化且未装背包） */
    static boolean canEquipBackpack(Cow cow) {
        return TameableHelper.isTamed(cow) && !TameableHelper.hasBackpack(cow);
    }

    /** 羊：是否可被牧羊犬驱赶（预留接口，暂不实现完整逻辑） */
    static boolean canBeHerded(Sheep sheep) {
        return TameableHelper.isTamed(sheep);
    }

    /** 猪：预留接口，暂不实现 */
    static boolean canBeControlled(Pig pig) {
        return TameableHelper.isTamed(pig);
    }
}
