package me.alexliudev.ycithelper;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

@Config(name = "ycithelper")
public class ModConfig implements ConfigData {
    private boolean enableLogFilter = true;
    private boolean enableLog = false;
    private boolean enableAutoFishing = false;
    private boolean enableAutoScan = false;
    private boolean enablePersistentFishingRedrop = false;
    private final List<BlockPos> fishes = new ArrayList<>();
    private int persistentFishingTimeout = 8;

    public boolean isEnableLog() {
        return enableLog;
    }

    public void setEnableLog(boolean enableLog) {
        this.enableLog = enableLog;
    }

    public boolean isEnableLogFilter() {
        return enableLogFilter;
    }

    public List<BlockPos> getFishes() {
        return fishes;
    }

    public void setEnableLogFilter(boolean enableLogFilter) {
        this.enableLogFilter = enableLogFilter;
    }

    public boolean isEnableAutoFishing() {
        return enableAutoFishing;
    }

    public void setEnableAutoFishing(boolean enableAutoFishing) {
        this.enableAutoFishing = enableAutoFishing;
    }

    public void setEnableAutoScan(boolean enableAutoScan) {
        this.enableAutoScan = enableAutoScan;
    }

    public boolean isEnableAutoScan() {
        return enableAutoScan;
    }

    public int getPersistentFishingTimeout() {
        return persistentFishingTimeout;
    }

    public void setPersistentFishingTimeout(int persistentFishingTimeout) {
        this.persistentFishingTimeout = persistentFishingTimeout;
    }

    public void setEnablePersistentFishingRedrop(boolean enablePersistentFishingRedrop) {
        this.enablePersistentFishingRedrop = enablePersistentFishingRedrop;
    }

    public boolean isEnablePersistentFishingRedrop() {
        return enablePersistentFishingRedrop;
    }
}