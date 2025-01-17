package io.github.thatrobin.ra_additions.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ra_additions.powers.BossBarPower;
import io.github.thatrobin.ra_additions.util.BossBarHudRenderOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {

    @Shadow @Final
    Map<UUID, ClientBossBar> bossBars;

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void renderStart(DrawContext context, CallbackInfo ci) {
        int i = context.getScaledWindowWidth();
        int j = 12;
        PlayerEntity player = this.client.player;
        List<BossBarPower> bars = PowerHolderComponent.getPowers(player, BossBarPower.class);
        bars.sort(Comparator.comparingInt(o -> o.getRenderSettings().getPriority()));
        bars = bars.stream().filter(BossBarPower::shouldRender).collect(Collectors.toList());
        for (BossBarPower bossBarPower : bars) {
            int k = i / 2 - 91;
            int l = j;
            if (bossBarPower.getText() == null) {
                l -= 9;
            }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, bossBarPower.getRenderSettings().getSpriteLocation());
            renderBossBarResource(context, k, l, bossBarPower);
            j += 10;
            if (bossBarPower.getText() != null) {
                Text text = bossBarPower.getText();
                int m = this.client.textRenderer.getWidth(text);
                int n = i / 2 - m / 2;
                int o = l - 9;
                context.drawTextWithShadow(this.client.textRenderer, text, n, o, 0xFFFFFF);
                j += this.client.textRenderer.fontHeight;
            }
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, CallbackInfo ci) {
        int i = this.client.getWindow().getScaledWidth();
        int j = 12;
        PlayerEntity player = this.client.player;
        List<BossBarPower> bars = PowerHolderComponent.getPowers(player, BossBarPower.class);
        bars.sort(Comparator.comparingInt(o -> o.getRenderSettings().getPriority()));
        bars = bars.stream().filter(BossBarPower::shouldRender).collect(Collectors.toList());
        for (BossBarPower bossBarPower : bars) {
            int k = i / 2 - 91;
            int l = j;
            if (bossBarPower.getText() == null) {
                l -= 9;
            }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, bossBarPower.getRenderSettings().getSpriteLocation());
            renderBossBarResource(context, k, l, bossBarPower);
            j += 10;
            if (bossBarPower.getText() != null) {
                Text text = bossBarPower.getText();
                int m = this.client.textRenderer.getWidth(text);
                int n = i / 2 - m / 2;
                int o = l - 9;
                context.drawTextWithShadow(this.client.textRenderer, text, n, o, 0xFFFFFF);
                j += this.client.textRenderer.fontHeight;
            }
        }
    }

    private void renderBossBarResource(DrawContext context, int x, int y, BossBarPower bossBarPower) {

        context.drawTexture(bossBarPower.getRenderSettings().getSpriteLocation(), x, y, 0, bossBarPower.getRenderSettings().getBarIndex() * 5 * 2, 182, 5);
        int i;
        if ((i = (int) (bossBarPower.getPercentage() * 183.0f)) > 0) {
            if(bossBarPower.getRenderSettings().isInverted()) {
                i = 183 - i;
            }
            context.drawTexture(bossBarPower.getRenderSettings().getSpriteLocation(), x, y, 0, bossBarPower.getRenderSettings().getBarIndex() * 5 * 2 + 5, i, 5);
        }
        List<BossBarHudRenderOverlay> overlays = bossBarPower.getRenderSettings().getOverlays();
        overlays.sort(Comparator.comparingInt(BossBarHudRenderOverlay::getPriority));
        for (BossBarHudRenderOverlay overlay : overlays) {
            if(overlay.shouldRender(this.client.player)) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.setShaderTexture(0, overlay.getSpriteLocation());
                context.drawTexture(overlay.getSpriteLocation(), x, y, 0, overlay.getBarIndex() * 5 * 2, 182, 5);
                if ((i = (int) (bossBarPower.getPercentage() * 183.0f)) > 0) {
                    if(bossBarPower.getRenderSettings().isInverted()) {
                        i = 183 - i;
                    }
                    context.drawTexture(bossBarPower.getRenderSettings().getSpriteLocation(), x, y, 0, overlay.getBarIndex() * 5 * 2 + 5, i, 5);
                }
            }
        }
    }

}
