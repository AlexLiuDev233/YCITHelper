package me.alexliudev.ycithelper;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "ycithelper")
public class ModConfig implements ConfigData {
    private boolean enableLogFilter = true;

    public boolean isEnableLogFilter() {
        return enableLogFilter;
    }

    public void setEnableLogFilter(boolean enableLogFilter) {
        this.enableLogFilter = enableLogFilter;
    }
}