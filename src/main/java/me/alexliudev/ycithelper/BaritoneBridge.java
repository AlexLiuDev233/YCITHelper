package me.alexliudev.ycithelper;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 由于作者不想在编译时去编译Baritone，所以此处使用魔法来访问Baritone
public class BaritoneBridge {
    private static final Logger log = LoggerFactory.getLogger(BaritoneBridge.class);
    private static Method setGoalMethod;
    private static Method pathMethod;
    private static Method isActiveMethod;
    private static Method mostRecentGoalMethod;
    private static Object customGoalProcessObject;
    private static boolean failedInit = false;
    private static List<Block> blocksToAvoid;
    private static Object allowSprintObject,allowBreakObject,sprintInWaterObject;

    private static Constructor<?> goalBlockClassConstructor;

    private BaritoneBridge() {}

    public static boolean isBaritoneLoaded() {
        if (failedInit) return false;
        return FabricLoader.getInstance().isModLoaded("baritone");
    }
    public static boolean initialize() {
        try {
            Class<?> baritoneApi = Class.forName("baritone.api.BaritoneAPI");
            // 配置设置
            Object baritoneSettings = baritoneApi.getDeclaredMethod("getSettings").invoke(null);
            allowSprintObject = baritoneSettings.getClass().getDeclaredField("allowSprint").get(baritoneSettings);

            allowBreakObject = baritoneSettings.getClass().getDeclaredField("allowBreak").get(baritoneSettings);

            sprintInWaterObject = baritoneSettings.getClass().getDeclaredField("sprintInWater").get(baritoneSettings);

            Object blocksToAvoidObject = baritoneSettings.getClass().getDeclaredField("blocksToAvoid").get(baritoneSettings);
            blocksToAvoid = (List<Block>) blocksToAvoidObject.getClass().getDeclaredField("value").get(blocksToAvoidObject);
            // 设置目的地API获取
            Object baritoneProvider = baritoneApi.getDeclaredMethod("getProvider").invoke(null);
            Object baritone = baritoneProvider.getClass().getDeclaredMethod("getPrimaryBaritone").invoke(baritoneProvider);
            customGoalProcessObject = baritone.getClass().getDeclaredMethod("getCustomGoalProcess").invoke(baritone);
            setGoalMethod = customGoalProcessObject.getClass().getDeclaredMethod("setGoal", Class.forName("baritone.api.pathing.goals.Goal"));
            pathMethod = customGoalProcessObject.getClass().getDeclaredMethod("path");
            isActiveMethod = customGoalProcessObject.getClass().getDeclaredMethod("isActive");// 内部API，黑魔法是这样的
            mostRecentGoalMethod = customGoalProcessObject.getClass().getDeclaredMethod("mostRecentGoal");// 内部API，黑魔法是这样的
            // 方块目的地API获取
            goalBlockClassConstructor = Class.forName("baritone.api.pathing.goals.GoalBlock").getConstructor(BlockPos.class);
            return true;
        } catch (Throwable e) {
            log.error("Failed init Baritone!", e);
            failedInit = true;
            return false;
        }
    }

    private static final Map<Object, Runnable> goalAndPathCallback = new HashMap<>();
    private static boolean haveGoalTask = false;

    public static boolean isHaveGoalTask() {
        return haveGoalTask;
    }

    public static void setGoalAndPath(BlockPos pos, Runnable callback) {
        if (haveGoalTask || isGoaling()) {
            // 取消!
            return;
        }
        try {
            Object goal = goalBlockClassConstructor.newInstance(pos);
            setGoalMethod.invoke(customGoalProcessObject, goal);
            pathMethod.invoke(customGoalProcessObject);
            goalAndPathCallback.put(goal,callback);
            haveGoalTask = true;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isGoaling() {
        try {
            return (boolean) isActiveMethod.invoke(customGoalProcessObject);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static void onGoalComplete() {
        haveGoalTask = false;
        try {
            goalAndPathCallback.remove(mostRecentGoalMethod.invoke(customGoalProcessObject)).run();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static void onClientWorldLoaded() {
        try {
            Registries.BLOCK.iterateEntries(BlockTags.FENCE_GATES).forEach(tag -> blocksToAvoid.add(tag.value()));
            Registries.BLOCK.iterateEntries(BlockTags.DOORS).forEach(tag -> blocksToAvoid.add(tag.value()));
            Registries.BLOCK.iterateEntries(BlockTags.TRAPDOORS).forEach(tag -> blocksToAvoid.add(tag.value()));
            allowSprintObject.getClass().getDeclaredField("value").set(allowSprintObject, true);
            allowBreakObject.getClass().getDeclaredField("value").set(allowBreakObject, false);
            sprintInWaterObject.getClass().getDeclaredField("value").set(sprintInWaterObject, true);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
