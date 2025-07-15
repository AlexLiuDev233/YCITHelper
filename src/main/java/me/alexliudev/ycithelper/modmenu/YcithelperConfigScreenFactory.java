package me.alexliudev.ycithelper.modmenu;

import me.alexliudev.ycithelper.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.function.Function;

public class YcithelperConfigScreenFactory {
    private static final Function<Boolean, Text> textSupplier = value ->
            value ? Text.translatable("options.ycithelper.on") : Text.translatable("options.ycithelper.off");

    public static Screen build(Screen parent) {

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("options.ycithelper.title"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory configCat = builder.getOrCreateCategory(Text.translatable("options.ycithelper.config"));

        configCat.addEntry(entryBuilder.startTextDescription(Text.translatable("options.ycithelper.description1")).build());//提示
        configCat.addEntry(entryBuilder.startTextDescription(Text.translatable("options.ycithelper.description2")).build());
        configCat.addEntry(entryBuilder.startTextDescription(Text.translatable("options.ycithelper.description3")).build());
        configCat.addEntry(entryBuilder.startTextDescription(Text.translatable("options.ycithelper.description4")).build());

        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        // Enable Log Filter
        BooleanListEntry logFilterToggle = entryBuilder.startBooleanToggle(Text.translatable("options.ycithelper.enable.logfilter"), config.isEnableLogFilter())
                .setYesNoTextSupplier(textSupplier)
                .setDefaultValue(true)
                .setTooltip(
                        Text.translatable("options.ycithelper.config.tooltip.enableLogFilter1"),
                        Text.translatable("options.ycithelper.config.tooltip.enableLogFilter2")
                )
                .setSaveConsumer(value -> {
                    config.setEnableLogFilter(value);
                    AutoConfig.getConfigHolder(ModConfig.class).save();
                })
                .build();

        SubCategoryBuilder categoryBasic = entryBuilder.startSubCategory(Text.translatable("options.ycithelper.category.basic"));
        categoryBasic.add(logFilterToggle);
        configCat.addEntry(categoryBasic.build());
        return builder.build();
    }
}
