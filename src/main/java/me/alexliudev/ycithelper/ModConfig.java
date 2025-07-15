package me.alexliudev.ycithelper;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "ycithelper")
public class ModConfig implements ConfigData {
    private boolean enableLogFilter = true;
    private boolean enableAutoFishing = false;

    public boolean isEnableLogFilter() {
        return enableLogFilter;
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
}