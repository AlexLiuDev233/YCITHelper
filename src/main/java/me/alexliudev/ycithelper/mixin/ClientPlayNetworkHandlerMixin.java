package me.alexliudev.ycithelper.mixin;

import me.alexliudev.ycithelper.BaritoneBridge;
import me.alexliudev.ycithelper.ModConfig;
import me.alexliudev.ycithelper.YcithelperClient;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
            String content = packet.text().getString();
            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            if (YcithelperClient.tryMoving) return;// 正在尝试移动位置
            if (!config.isEnableAutoFishing()) return;
            if (content.contains("点击")) {
                if (config.isEnableLog()) MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("持久钓鱼：结束定时! 原因：收到Subtitle"));
                processQTE(config);
                YcithelperClient.successFishing = true;//强制重置持久钓鱼
                YcithelperClient.scheduleOfPersistentFishing = 0;
                return;
            }
            if (!content.contains("右键")) return;
            if (client.player == null) return;
            if (client.player.fishHook == null) return;
            if (config.isEnableLog()) MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("持久钓鱼：结束定时! 原因：收到Subtitle"));
            YcithelperClient.successFishing = true;
            YcithelperClient.useItem(client);
        }
    }

    @Unique
    private void processQTE(ModConfig config) {
        if (config.isEnableLog())
            client.inGameHud.getChatHud().addMessage(Text.of("自动钓鱼QTE：模拟右键!"));
        if (client.player == null) return;
        if (client.world == null) return;
        client.player.swingHand(Hand.MAIN_HAND);
    }

    @Inject(method = "onGameJoin", at = @At("TAIL"))
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        if (BaritoneBridge.isBaritoneLoaded())
            BaritoneBridge.onClientWorldLoaded();
    }
}
