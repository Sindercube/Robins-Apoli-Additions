package io.github.thatrobin.ra_additions_goals.factories.goals;

import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.Goal;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalFactory;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.mob.MobEntity;

public class C_EatGrassGoal extends Goal {

    public C_EatGrassGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.setGoal(new EatGrassGoal((MobEntity) livingEntity) {
            @Override
            public boolean canStart() {
                return super.canStart() && doesApply(this.mob);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("eat_grass"), new SerializableDataExt()
                .add("priority", "The priority of the goal, the lower, the more important.", SerializableDataTypes.INT, 0),
                data ->
                        (type, entity) -> new C_EatGrassGoal(type, entity, data.getInt("priority")))
                .allowCondition();
    }

}