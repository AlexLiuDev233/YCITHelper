package me.alexliudev.ycithelper;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YcithelperClient implements ClientModInitializer {
    public static boolean tryMoving = false;
    public static int nextId = 0;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        // Init Spam Fix
        ((Logger) LogManager.getRootLogger()).addFilter(new LogFilter());
        // Init Baritone
        if (BaritoneBridge.isBaritoneLoaded()) {
            if (!BaritoneBridge.initialize()) return;// Baritone加载失败
            configHolder = AutoConfig.getConfigHolder(ModConfig.class);
            ClientTickEvents.END_CLIENT_TICK.register(this::onBaritoneTick);
            ClientReceiveMessageEvents.GAME.register((text, bool) -> {
                if (!"鱼群中没有鱼了...".equals(text.getString()) && !"你不在鱼群范围内钓鱼".equals(text.getString())) return;// 不要使用contains，否则如果服务端装了NCP，那样会导致其他人可以遥控你!
                goToNextPosition();
            });
        }
    }

    public static void goToNextPosition() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        tryMoving = true;
        if (client.player.fishHook != null) {
            useItem(client);
            if (client.player.fishHook != null) useItem(client);
        }
        ModConfig config = configHolder.getConfig();
        BlockPos targetPos = null;
        try {
            targetPos = config.getFishes().get(nextId);
            nextId++;
        } catch (IndexOutOfBoundsException e) {
            nextId = 0;
            try {
                targetPos = config.getFishes().getFirst();
            } catch (IndexOutOfBoundsException e2) {
                // 配置文件没配置，取消操作
                tryMoving = false;
                return;
            }
        }
        if (targetPos == null) return;

        BaritoneBridge.setGoalAndPath(targetPos, () -> {
            if (config.isEnableLog()) MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("自动寻路：回调x1"));
            if (client.world == null) return;
            DisplayEntity.TextDisplayEntity textDisplay = null;
            float distance = 0;
            // 回调喵
            for (Entity entity : client.world.getEntities()) {
                if (!(entity instanceof DisplayEntity.TextDisplayEntity display)) continue;
                if (!display.getText().getString().contains("鱼群")) continue;
                float displayDistance = display.distanceTo(client.player);
                if (distance < displayDistance && textDisplay != null) continue;
                textDisplay = display;
                distance = displayDistance;
            }
            if (textDisplay == null) {
                //下一点
                goToNextPosition();
                return;
            }

            BaritoneBridge.setGoalAndPath(textDisplay.getBlockPos(), () -> {//拜YC bug所赐，我们得过去
                if (config.isEnableLog()) MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("自动寻路：回调x2"));
                client.player.setPitch(64.0F);
                tryMoving = false;
                useItem(client);
            });
        });
    }

    private int schedule = 0;
    public void onBaritoneTick(MinecraftClient client) {
        schedule++;
        if (schedule >= 10) {
            schedule = 0;
            // 处理 自动钓鱼 时每10ticks跳一次(Windows默认长按空格，500ms发一次)
            processJump(client);
            // 处理 路径点扫描(这里只是借用10ticks,玩家不可能飞出去，所以慢慢跑就行)
            processScan(client);
        }
        // 处理 Baritone 导航相关，实现伪事件
        if (!BaritoneBridge.isHaveGoalTask()) return;
        if (BaritoneBridge.isGoaling()) return;
        BaritoneBridge.onGoalComplete();
    }

    private final List<Float> distances = new ArrayList<>();
    private final Map<Float, DisplayEntity.TextDisplayEntity> distanceWithEntity = new HashMap<>();

    private static ConfigHolder<ModConfig> configHolder;

    private void processScan(MinecraftClient client) {
        if (client.world == null) return;
        if (client.player == null) return;
        ModConfig config = configHolder.getConfig();
        if (!config.isEnableAutoScan()) return;
        distances.clear();
        distanceWithEntity.clear();

        for (Entity entity : client.world.getEntities()) {
            if (!(entity instanceof DisplayEntity.TextDisplayEntity display)) continue;
            if (!display.getText().getString().contains("鱼群")) continue;
            float displayDistance = display.distanceTo(client.player);
            distances.add(displayDistance);
            distanceWithEntity.put(displayDistance, display);
        }

        distances.sort(null);
        List<BlockPos> locations = config.getFishes();
        boolean updated = false;
        for (float distance : distances) {
            BlockPos pos = distanceWithEntity.get(distance).getBlockPos();
            if (locations.contains(pos)) continue;
            locations.add(pos);
            updated = true;
        }
        if (updated)
            configHolder.save();
    }

    private void processJump(MinecraftClient client) {
        if (client.player == null) return;
        if (client.player.fishHook == null) return;
        if (client.world == null) return;
        if (client.getNetworkHandler() == null) return;
        if (client.world.getBlockState(client.player.getBlockPos().add(0,1,0)).isAir()) return;
        client.player.jump();
        client.player.setOnGround(false);
        client.getNetworkHandler().sendPacket(
                new PlayerMoveC2SPacket.Full(
                        client.player.getX(),
                        client.player.getY(),
                        client.player.getZ(),
                        client.player.getYaw(),
                        client.player.getPitch(),
                        client.player.isOnGround(),
                        false
                )
        );
    }

    public static void useItem(MinecraftClient client) {
        if (client.player == null) return;
        if (client.world == null) return;
        if (client.interactionManager == null) return;
        if (client.player.getInventory().selectedSlot != 5 && client.getNetworkHandler() != null) {
            client.player.getInventory().selectedSlot = 5;
            client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(5));
        }
        client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);

    }
}
