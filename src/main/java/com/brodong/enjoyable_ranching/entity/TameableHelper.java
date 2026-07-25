package com.brodong.enjoyable_ranching.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public class TameableHelper {

    private static final String KEY_TAMED = "enjoyable_ranching:tamed";
    private static final String KEY_OWNER = "enjoyable_ranching:owner";
    private static final String KEY_BACKPACK = "enjoyable_ranching:backpack";

    public static boolean isTamed(Animal animal) {
        return animal.getPersistentData().getBoolean(KEY_TAMED);
    }

    public static void setTamed(Animal animal, boolean tamed) {
        animal.getPersistentData().putBoolean(KEY_TAMED, tamed);
    }

    @Nullable
    public static UUID getOwnerUUID(Animal animal) {
        String raw = animal.getPersistentData().getString(KEY_OWNER);
        if (raw.isEmpty()) return null;
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void setOwnerUUID(Animal animal, @Nullable UUID owner) {
        if (owner != null) {
            animal.getPersistentData().putString(KEY_OWNER, owner.toString());
        } else {
            animal.getPersistentData().remove(KEY_OWNER);
        }
    }

    public static boolean isOwner(Animal animal, Player player) {
        UUID owner = getOwnerUUID(animal);
        return owner != null && owner.equals(player.getUUID());
    }

    /**
     * 尝试驯化：1/TAMING_CHANCE 概率成功，成功时设置主人
     * @return true 如果本次调用成功驯化
     */
    public static boolean tryTame(Animal animal, Player tamer) {
        if (isTamed(animal)) return false;
        if (animal.getRandom().nextInt(Tameable.TAMING_CHANCE) == 0) {
            setTamed(animal, true);
            setOwnerUUID(animal, tamer.getUUID());
            return true;
        }
        return false;
    }

    // ==================== 牛背包 ====================

    public static boolean hasBackpack(Cow cow) {
        return cow.getPersistentData().getBoolean(KEY_BACKPACK);
    }

    public static void setHasBackpack(Cow cow, boolean has) {
        cow.getPersistentData().putBoolean(KEY_BACKPACK, has);
    }

    // ==================== 饲槽取食驯化 ====================

    /**
     * 动物从饲槽取食后尝试驯化，找最近的玩家当主人。
     */
    public static void tryTameFromTrough(Animal animal) {
        if (isTamed(animal)) return;
        if (animal.level() instanceof ServerLevel level) {
            Player nearest = level.getNearestPlayer(animal, 10.0);
            if (nearest != null) {
                tryTame(animal, nearest);
            }
        }
    }
}
