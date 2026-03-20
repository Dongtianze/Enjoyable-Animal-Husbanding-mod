package com.brodong.enjoyable_animal_husbanding.mixin;

import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import org.spongepowered.asm.mixin.Mixin;


import java.util.List;

@Mixin(BreedGoal.class)
public abstract class BreedGoalMixin extends Goal {
}
