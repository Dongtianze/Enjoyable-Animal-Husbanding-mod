package com.brodong.enjoyable_ranching.event;

import com.brodong.enjoyable_ranching.EnjoyableRanching;
import com.brodong.enjoyable_ranching.GenderHelper;
import com.brodong.enjoyable_ranching.SatietyHelper;
import com.brodong.enjoyable_ranching.entity.CowBackpackContainer;
import com.brodong.enjoyable_ranching.entity.Tameable;
import com.brodong.enjoyable_ranching.entity.TameableHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnjoyableRanching.MODID)
public class BreedingEvents {

    @SubscribeEvent
    public static void onBabyEntitySpawn(BabyEntitySpawnEvent event) {
        Mob parentA = event.getParentA();
        Mob parentB = event.getParentB();
        if (!(parentA instanceof Animal) || !(parentB instanceof Animal)) return;

        if (GenderHelper.getGender(parentA) == GenderHelper.getGender(parentB)) {
            event.setCanceled(true);
            return;
        }

        if (parentA instanceof Chicken && parentB instanceof Chicken) {
            event.setCanceled(true);
            if (parentA.level() instanceof ServerLevel level
                    && level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                level.addFreshEntity(new ExperienceOrb(level,
                        parentA.getX(), parentA.getY(), parentA.getZ(),
                        parentA.getRandom().nextInt(7) + 1));
            }
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getTarget() instanceof Animal animal)) return;

        Player player = event.getEntity();
        ItemStack held = player.getItemInHand(event.getHand());

        // ====== 牛：装背包（箱子右键已驯化的牛） ======
        if (animal instanceof Cow cow && held.is(Items.CHEST) && Tameable.canEquipBackpack(cow)) {
            TameableHelper.setHasBackpack(cow, true);
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            event.setCanceled(true);
            return;
        }

        // ====== 牛：打开背包 ======
        if (held.isEmpty() && animal instanceof Cow cow && TameableHelper.hasBackpack(cow)) {
            CowBackpackContainer inv = new CowBackpackContainer(cow);
            player.openMenu(new SimpleMenuProvider(
                    (id, inv2, p) -> new ChestMenu(MenuType.GENERIC_9x1, id, inv2, inv, 1),
                    Component.translatable("container.enjoyable_ranching.cow_backpack")));
            event.setCanceled(true);
            return;
        }

        // ====== 喂食逻辑 ======
        if (held.isEmpty() || !animal.isFood(held)) return;

        if (animal.getAge() < 0) {
            SatietyHelper.fillSatiety(animal);
            if (Tameable.isTameable(animal)) {
                TameableHelper.tryTame(animal, player);
            }
            return;
        }

        event.setCanceled(true);
        SatietyHelper.fillSatiety(animal);
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }
        animal.playSound(animal.getEatingSound(held), 1.0F, 1.0F);

        if (Tameable.isTameable(animal)) {
            TameableHelper.tryTame(animal, player);
        }
    }

    /** 捕猎奖励 + 牛死亡掉落背包物品 */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        var entity = event.getEntity();
        var attacker = event.getSource().getEntity();

        // 捕猎者饱食度回满
        if (attacker instanceof Wolf || attacker instanceof Fox
                || attacker instanceof Cat || attacker instanceof net.minecraft.world.entity.animal.Ocelot) {
            SatietyHelper.fillSatiety((Animal) attacker);
        }

        // 带背包的牛死亡时掉落物品
        if (entity instanceof Cow cow && TameableHelper.hasBackpack(cow)) {
            CowBackpackContainer.dropItemsOnDeath(cow);
        }
    }
}
