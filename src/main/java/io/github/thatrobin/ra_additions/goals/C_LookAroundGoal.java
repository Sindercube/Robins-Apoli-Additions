package io.github.thatrobin.ra_additions.goals;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.Goal;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;
import io.github.thatrobin.ra_additions.goals.factories.GoalType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.mob.MobEntity;

import java.util.function.Predicate;

public class C_LookAroundGoal extends Goal {

    public Predicate<Entity> condition;

    public C_LookAroundGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority, Predicate<Entity> condition) {
        super(goalType, livingEntity);
        this.setPriority(priority);
        this.condition = condition;
        this.setGoal(new DDLookAroundGoal((MobEntity) livingEntity));
    }

    @Override
    public boolean doesApply(Entity entity){
        return condition == null || condition.test(entity);
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("look_around"), new SerializableData()
                .add("priority", SerializableDataTypes.INT, 0)
                .add("condition", ApoliDataTypes.ENTITY_CONDITION, null),
                data ->
                        (type, entity) -> new C_LookAroundGoal(type, entity, data.getInt("priority"), data.get("condition")));
    }

    class DDLookAroundGoal extends LookAroundGoal {

        private final MobEntity mob;

        public DDLookAroundGoal(MobEntity mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public boolean canStart() {
            return super.canStart() && doesApply(mob);
        }
    }

}