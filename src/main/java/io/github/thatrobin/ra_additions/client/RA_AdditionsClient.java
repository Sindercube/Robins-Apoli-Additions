package io.github.thatrobin.ra_additions.client;

import io.github.thatrobin.ra_additions.networking.RAA_ModPacketS2C;
import io.github.thatrobin.ra_additions.util.RenderStatBarPowerOverlay;
import io.github.thatrobin.ra_additions.util.RenderValuePowerOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

@Environment(EnvType.CLIENT)
public class RA_AdditionsClient implements ClientModInitializer {

    public static boolean isServerRunningRAA = false;

    @Override
    public void onInitializeClient() {
        RAA_ModPacketS2C.register();
        HudRenderCallback.EVENT.register(new RenderValuePowerOverlay());
        HudRenderCallback.EVENT.register(new RenderStatBarPowerOverlay());
    }

}
