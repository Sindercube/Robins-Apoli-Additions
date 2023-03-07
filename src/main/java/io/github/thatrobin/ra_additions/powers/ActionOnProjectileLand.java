package io.github.thatrobin.ra_additions.powers;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnProjectileLand extends Power {

    private final Predicate<CachedBlockPosition> blockCondition;
    private final Consumer<Entity> entityAction;
    private final Consumer<Entity> selfAction;
    private final Consumer<Triple<World, BlockPos, Direction>> blockAction;
    private final Identifier projectile;

    public ActionOnProjectileLand(PowerType<?> type, LivingEntity entity, Predicate<CachedBlockPosition> blockCondition, Consumer<Entity> entityAction, Consumer<Entity> selfAction, Consumer<Triple<World, BlockPos, Direction>> blockAction, Identifier projectile) {
        super(type, entity);
        this.blockCondition = blockCondition;
        this.entityAction = entityAction;
        this.selfAction = selfAction;
        this.blockAction = blockAction;
        this.projectile = projectile;
    }

    public boolean doesApply(BlockPos pos) {
        CachedBlockPosition cbp = new CachedBlockPosition(entity.world, pos, true);
        return doesApply(cbp);
    }

    public boolean doesApply(CachedBlockPosition pos) {
        return blockCondition == null || blockCondition.test(pos);
    }

    public void executeActions(BlockPos pos, Direction dir, Entity projectileEntity) {
        if (selfAction != null) {
            selfAction.accept(entity);
        }
        if (entityAction != null) {
            entityAction.accept(projectileEntity);
        }
        if (blockAction != null) {
            blockAction.accept(Triple.of(entity.world, pos, dir));
        }
    }

    public EntityType<?> getProjectile() {
        return Registries.ENTITY_TYPE.get(projectile);
    }

    @SuppressWarnings("rawtypes")
    public static PowerFactory createFactory(String label) {
        return new PowerFactory<>(RA_Additions.identifier("action_on_projectile_land"),
                new SerializableDataExt(label)
                        .add("projectile", "The identifier of the projectile entity.", SerializableDataTypes.IDENTIFIER, null)
                        .add("entity_action", "The entity action to be executed on the projectile if specified.", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("self_action", "The entity action to be executed on the player if specified.", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("block_action", "The block action to be executed if specified.", ApoliDataTypes.BLOCK_ACTION, null)
                        .add("block_condition", "If specified, only execute the specified actions if the block condition is fulfilled.", ApoliDataTypes.BLOCK_CONDITION, null),
                data ->
                        (type, entity) -> new ActionOnProjectileLand(type, entity, data.get("block_condition"), data.get("entity_action"), data.get("self_action"), data.get("block_action"), data.getId("projectile")))
                .allowCondition();
    }
}
