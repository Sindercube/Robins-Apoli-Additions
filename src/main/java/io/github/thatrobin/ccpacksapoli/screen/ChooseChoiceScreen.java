package io.github.thatrobin.ccpacksapoli.screen;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.thatrobin.ccpacksapoli.choice.Choice;
import io.github.thatrobin.ccpacksapoli.choice.ChoiceLayer;
import io.github.thatrobin.ccpacksapoli.choice.ChoiceRegistry;
import io.github.thatrobin.ccpacksapoli.networking.CCPacksModPackets;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ChooseChoiceScreen extends ChoiceDisplayScreen {

    private final ArrayList<ChoiceLayer> layerList;
    private final int currentLayerIndex;
    private int currentChoice = 0;
    private final List<Choice> ChoiceSelection;
    private final int maxSelection;

    public ChooseChoiceScreen(ArrayList<ChoiceLayer> layerList, int currentLayerIndex, boolean showDirtBackground) {
        super(new TranslatableText("ccpacks.screen.choose_Choice"), showDirtBackground);
        this.layerList = layerList;
        this.currentLayerIndex = currentLayerIndex;
        this.ChoiceSelection = new ArrayList<>(0);
        ChoiceLayer currentLayer = layerList.get(currentLayerIndex);
        List<Identifier> ChoiceIdentifiers = currentLayer.getChoices(MinecraftClient.getInstance().player);
        ChoiceIdentifiers.forEach(ChoiceId -> {
            Choice Choice = ChoiceRegistry.get(ChoiceId);
            this.ChoiceSelection.add(Choice);
        });
        maxSelection = ChoiceSelection.size();
        if(maxSelection == 0) {
            openNextLayerScreen();
        }
        Choice newChoice = getCurrentChoiceInternal();
        showChoice(newChoice, layerList.get(currentLayerIndex));
    }

    private void openNextLayerScreen() {
        MinecraftClient.getInstance().setScreen(null);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        addDrawableChild(new ButtonWidget(guiLeft - 40,this.height / 2 - 10, 20, 20, new LiteralText("<"), b -> {
            currentChoice = (currentChoice - 1 + maxSelection) % maxSelection;
            Choice newChoice = getCurrentChoiceInternal();
            description = newChoice.getDescription();
            showChoice(newChoice, layerList.get(currentLayerIndex));
            MinecraftClient.getInstance().setScreen(this);
        }));
        addDrawableChild(new ButtonWidget(guiLeft + windowWidth + 20, this.height / 2 - 10, 20, 20, new LiteralText(">"), b -> {
            currentChoice = (currentChoice + 1) % maxSelection;
            Choice newChoice = getCurrentChoiceInternal();
            description = newChoice.getDescription();
            showChoice(newChoice, layerList.get(currentLayerIndex));
            MinecraftClient.getInstance().setScreen(this);
        }));
        addDrawableChild(new ButtonWidget(guiLeft + windowWidth / 2 - 50, guiTop + windowHeight + 5, 100, 20, new TranslatableText("ccpacks.gui.select"), b -> {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeString(getCurrentChoice().getIdentifier().toString());
            buf.writeString(layerList.get(currentLayerIndex).getIdentifier().toString());
            ActionFactory<Entity>.Instance action = getCurrentChoice().getAction();
            if(action != null) {
                action.accept(MinecraftClient.getInstance().player);
            }
            ClientPlayNetworking.send(CCPacksModPackets.CHOOSE_CHOICE, buf);
            openNextLayerScreen();
        }));

    }

    private Choice getCurrentChoiceInternal() {
        return ChoiceSelection.get(currentChoice);
    }
    private ChoiceLayer getCurrentLayerInternal() {
        return layerList.get(currentLayerIndex);
    }



    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(maxSelection == 0) {
            openNextLayerScreen();
            return;
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
}