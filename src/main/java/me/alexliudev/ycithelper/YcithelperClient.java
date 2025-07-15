package me.alexliudev.ycithelper;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class YcithelperClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        // Init Spam Fix
        ((Logger) LogManager.getRootLogger()).addFilter(new LogFilter());
    }
}
