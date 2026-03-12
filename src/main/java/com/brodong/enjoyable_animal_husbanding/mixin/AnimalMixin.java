package com.brodong.enjoyable_animal_husbanding.mixin;

import com.brodong.enjoyable_animal_husbanding.Gender;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalMixin extends AgeableMob {
    @Unique
    Logger LOGGER = LogUtils.getLogger();
    @Unique
    Gender gender;
    protected AnimalMixin(EntityType<? extends AgeableMob> p_146738_, Level p_146739_) {
        super(p_146738_, p_146739_);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void InitMixin(EntityType p_27557_, Level p_27558_, CallbackInfo ci){
        gender = Gender.Male;
        LOGGER.info("{}",gender);
    }
}
