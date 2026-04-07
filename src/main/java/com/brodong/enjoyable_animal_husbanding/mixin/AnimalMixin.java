package com.brodong.enjoyable_animal_husbanding.mixin;

import com.brodong.enjoyable_animal_husbanding.accessor.Gender;
import com.brodong.enjoyable_animal_husbanding.accessor.GenderAccessor;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(Animal.class)
public abstract class AnimalMixin implements GenderAccessor {

    // 2. 用 @Unique 标记新增内容，避免冲突
    @Unique
    private static final Random RANDOM = new Random();

    @Unique
    private Gender gender;

    @Unique
    private Logger LOGGER = LogUtils.getLogger();

    // 3. 注入构造函数，随机初始化性别
    @Inject(method = "<init>", at = @At("RETURN"))
    private void yourmodid$initGender(EntityType<? extends Animal> type, Level level, CallbackInfo ci) {
        this.gender = RANDOM.nextBoolean() ? Gender.Male : Gender.Female;
        LOGGER.info("{}",gender);
    }

    // 4. 实现接口的 Getter/Setter
    @Override
    @Unique
    public Gender getGender() {
        return this.gender;
    }

    @Override
    @Unique
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Inject(method = "canMate", at = @At("HEAD"), cancellable = true)
    private void checkGenderBeforeMate(Animal p_27569_, CallbackInfoReturnable<Boolean> cir) {
        if (p_27569_ instanceof GenderAccessor partnerAccessor) {
            Gender myGender = this.getGender();
            Gender partnerGender = partnerAccessor.getGender();

            // 性别相同直接返回 false，取消交配
            if (myGender == partnerGender) {
                cir.setReturnValue(false);
            }
        }
        // 性别不同不做处理，继续执行原版 canMate 逻辑
    }
}
