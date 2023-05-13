package io.github.thatrobin.ra_additions.powers.factories;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.data.RAA_DataTypes;
import io.github.thatrobin.ra_additions.util.*;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class ItemConditions {

    public static void register() {
        register(new ConditionFactory<>(RA_Additions.identifier("is_block"),
                new SerializableDataExt(),
                (data, stack) -> stack.getItem() instanceof BlockItem
                ), "Checks if the item extends BlockItem (Checks if the item is a block).");

        register(new ConditionFactory<>(RA_Additions.identifier("evaluate_condition"), new SerializableDataExt()
                .add("item_condition", "The Identifier of the tag or condition file to be evaluated", SerializableDataTypes.STRING),
                (data, stack) -> {
                    String idStr = data.getString("item_condition");
                    if(idStr.startsWith("#")) {
                        Identifier id = Identifier.tryParse(idStr.substring(1));
                        Collection<ConditionType> conditions = ItemConditionTagManager.CONDITION_TAG_LOADER.getTag(id);
                        boolean result = true;
                        for (ConditionType condition : conditions) {
                            if(!condition.getCondition().test(stack)) {
                                result = false;
                            }
                        }
                        return result;
                    } else {
                        Identifier id = Identifier.tryParse(idStr);
                        ConditionFactory<ItemStack>.Instance condition =  ItemConditionRegistry.get(id).getCondition();
                        return condition.test(stack);
                    }
                }), "Evaluates an item condition that is stored in a file.");
    }

    @SuppressWarnings("SameParameterValue")
    private static void register(ConditionFactory<ItemStack> factory, String description) {
        DockyEntry entry = new DockyEntry()
                .setHeader("Condition Types")
                .setFactory(factory)
                .setDescription(description)
                .setType("item_condition_types");
        if(RA_Additions.getExamplePathRoot() != null) entry.setExamplePath(RA_Additions.getExamplePathRoot() + "\\testdata\\ra_additions\\conditions\\item\\" + factory.getSerializerId().getPath() + "_example.json");
        DockyRegistry.register(entry);
        Registry.register(ApoliRegistries.ITEM_CONDITION, factory.getSerializerId(), factory);
    }

}
