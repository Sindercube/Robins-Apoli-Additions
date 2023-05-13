package io.github.thatrobin.ra_additions.goals;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.Goal;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;
import io.github.thatrobin.ra_additions.goals.factories.GoalType;
import io.github.thatrobin.ra_additions.util.FluidEnum;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.tag.FluidTags;

public class C_SwimGoal extends Goal {


    public C_SwimGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, FluidEnum fluidEnum) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.setTicking();
        this.setGoal(new SwimGoal((MobEntity) livingEntity) {
            @Override
            public boolean canStart() {
                switch(fluidEnum) {
                    case LAVA -> {
                        return this.mob.isInLava() && doesApply(mob);
                    }
                    case WATER -> {
                        return this.mob.isTouchingWater() && this.mob.getFluidHeight(FluidTags.WATER) > this.mob.getSwimHeight() && doesApply(mob);
                    }
                    case BOTH -> {
                        return ((this.mob.isTouchingWater() && this.mob.getFluidHeight(FluidTags.WATER) > this.mob.getSwimHeight()) || this.mob.isInLava()) && doesApply(mob);
                    }
                }
                return false;
            }
        });
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("swim"), new SerializableDataExt()
                .add("priority", SerializableDataTypes.INT, 0)
                .add("fluid_type", SerializableDataType.enumValue(FluidEnum.class), FluidEnum.BOTH),
                data ->
                        (type, entity) -> new C_SwimGoal(type, entity, data.getInt("priority"), data.get("fluid_type")))
                .allowCondition();
    }

}
