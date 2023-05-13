package io.github.thatrobin.ra_additions.mixins;

import io.github.apace100.apoli.ApoliClient;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(value = ApoliClient.class, remap = false)
public interface ApoliClientAccessorMixin {

    static @Accessor
    HashMap<String, KeyBinding> getIdToKeyBindingMap() {
        throw new AssertionError();
    }

    static @Accessor
    void setIdToKeyBindingMap(HashMap<String, KeyBinding> ignoredMap) {
        throw new AssertionError();
    }
}
