package me.alexliudev.ycithelper.mixin;

import me.alexliudev.ycithelper.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler {
    protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
    }

    @Inject(method = "onSubtitle", at = @At("HEAD"))
    public void onSubtitle(SubtitleS2CPacket packet, CallbackInfo ci) {
        if (client.isOnThread()) {
            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            if (!config.isEnableAutoFishing()) return;
            if (!packet.text().getString().contains("右键")) return;
            if (client.player == null) return;
            if (client.player.fishHook == null) return;
            if (client.world == null) return;
            if (client.interactionManager == null) return;
            client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
        }
    }
}
