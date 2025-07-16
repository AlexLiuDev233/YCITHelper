package me.alexliudev.ycithelper.modmenu;

import me.alexliudev.ycithelper.BaritoneBridge;
import me.alexliudev.ycithelper.ModConfig;
import me.alexliudev.ycithelper.YcithelperClient;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

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
        // Enable Debug Log
        BooleanListEntry debugLogToggle = entryBuilder.startBooleanToggle(Text.translatable("options.ycithelper.enable.debuglog"), config.isEnableLog())
                .setYesNoTextSupplier(textSupplier)
                .setDefaultValue(false)
                .setTooltip(
                        Text.translatable("options.ycithelper.config.tooltip.debuglog")
                )
                .setSaveConsumer(value -> {
                    config.setEnableLog(value);
                    AutoConfig.getConfigHolder(ModConfig.class).save();
                })
                .build();

        SubCategoryBuilder categoryBasic = entryBuilder.startSubCategory(Text.translatable("options.ycithelper.category.basic"));
        categoryBasic.add(logFilterToggle);
        categoryBasic.add(debugLogToggle);
        configCat.addEntry(categoryBasic.build());


        // Enable Auto Fishing
        BooleanListEntry autoFishingToggle = entryBuilder.startBooleanToggle(Text.translatable("options.ycithelper.enable.autoFishing"), config.isEnableLogFilter())
                .setYesNoTextSupplier(textSupplier)
                .setDefaultValue(false)
                .setTooltip(
                        Text.translatable("options.ycithelper.config.tooltip.autoFishing1"),
                        Text.translatable("options.ycithelper.config.tooltip.autoFishing2")
                )
                .setSaveConsumer(value -> {
                    config.setEnableAutoFishing(value);
                    AutoConfig.getConfigHolder(ModConfig.class).save();
                })
                .build();
        // Enable Persistent Fishing
        IntegerListEntry persistentFishingTimeout = entryBuilder.startIntField(Text.translatable("options.ycithelper.enable.persistentFishing"), config.getPersistentFishingTimeout())
                .setMin(0)
                .setMax(60)
                .setDefaultValue(8)
                .setTooltip(
                        Text.translatable("options.ycithelper.config.tooltip.persistentFishing1"),
                        Text.translatable("options.ycithelper.config.tooltip.persistentFishing2"),
                        Text.translatable("options.ycithelper.config.tooltip.persistentFishing3")
                )
                .setSaveConsumer(value -> {
                    config.setPersistentFishingTimeout(value);
                    AutoConfig.getConfigHolder(ModConfig.class).save();
                })
                .build();
        SubCategoryBuilder categoryDangerous = entryBuilder.startSubCategory(Text.translatable("options.ycithelper.category.dangerous"));
        categoryDangerous.add(entryBuilder.startTextDescription(Text.translatable("options.ycithelper.category.dangerous.warning")).setColor(0xFFFF0000).build());// 君子协定
        categoryDangerous.add(autoFishingToggle);
        categoryDangerous.add(persistentFishingTimeout);
        // Baritone
        if (!BaritoneBridge.isBaritoneLoaded()) {
            categoryDangerous.add(entryBuilder.startTextDescription(Text.translatable("options.ycithelper.config.baritone.not_loaded")).build());
        } else {
            categoryDangerous.add(
                    entryBuilder.startBooleanToggle(Text.translatable("options.ycithelper.config.baritone.click_record"), true)
                            .setTooltip(
                                    Text.translatable("options.ycithelper.config.baritone.click_record.description1"),
                                    Text.translatable("options.ycithelper.config.baritone.click_record.description2"),
                                    Text.translatable("options.ycithelper.config.baritone.click_record.description3")
                            )
                            .setYesNoTextSupplier((value) -> {
                                if (value) return Text.translatable("options.ycithelper.config.baritone.add");
                                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                                if (player == null) return Text.translatable("options.ycithelper.config.baritone.add");
                                config.getFishes().add(player.getBlockPos());
                                AutoConfig.getConfigHolder(ModConfig.class).save();
                                MinecraftClient.getInstance().setScreen(build(parent));
                                return Text.translatable("options.ycithelper.config.baritone.add");
                            })
                            .build()
            );
            categoryDangerous.add(
                    entryBuilder.startBooleanToggle(Text.translatable("options.ycithelper.config.baritone.try_go_to"), true)
                            .setTooltip(
                                    Text.translatable("options.ycithelper.config.baritone.try_go_to.description")
                            )
                            .setYesNoTextSupplier((value) -> {
                                if (value) return Text.translatable("options.ycithelper.config.baritone.go");
                                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                                if (player == null) return Text.translatable("options.ycithelper.config.baritone.go");
                                YcithelperClient.goToNextPosition();
                                assert MinecraftClient.getInstance().currentScreen != null;
                                MinecraftClient.getInstance().currentScreen.close();
                                return Text.translatable("options.ycithelper.config.baritone.go");
                            })
                            .build()
            );
            categoryDangerous.add(
                    entryBuilder.startBooleanToggle(Text.translatable("options.ycithelper.config.baritone.auto_scan"), config.isEnableAutoScan())
                            .setDefaultValue(false)
                            .setTooltip(
                                    Text.translatable("options.ycithelper.config.baritone.auto_scan.description1"),
                                    Text.translatable("options.ycithelper.config.baritone.auto_scan.description2")
                            )
                            .setYesNoTextSupplier(textSupplier)
                            .setSaveConsumer(value -> {
                                config.setEnableAutoScan(value);
                                AutoConfig.getConfigHolder(ModConfig.class).save();
                            })
                            .build()
            );
            categoryDangerous.add(
                    entryBuilder.startBooleanToggle(Text.translatable("options.ycithelper.config.baritone.reset_all_positions"), true)
                            .setDefaultValue(false)
                            .setYesNoTextSupplier((value) -> {
                                if (value) return Text.translatable("options.ycithelper.config.baritone.clear");
                                config.getFishes().clear();
                                AutoConfig.getConfigHolder(ModConfig.class).save();
                                YcithelperClient.nextId = 0;
                                MinecraftClient.getInstance().setScreen(build(parent));
                                return Text.translatable("options.ycithelper.config.baritone.clear");
                            })
                            .build()
            );
        }
        configCat.addEntry(categoryDangerous.build());
        if (BaritoneBridge.isBaritoneLoaded()) {
            var categoryPositions = entryBuilder.startSubCategory(Text.translatable("options.ycithelper.category.baritone_positions"));
            buildPositionsCategory(categoryPositions, config, entryBuilder, parent);
            assert categoryPositions != null;
            configCat.addEntry(categoryPositions.build());
        }
        return builder.build();
    }

    private static void buildPositionsCategory(SubCategoryBuilder category, ModConfig config, ConfigEntryBuilder entryBuilder, Screen parent) {
        for (BlockPos pos : config.getFishes()) {
            category.add(
                    entryBuilder.startBooleanToggle(Text.of(pos.toShortString()), true)
                            .setYesNoTextSupplier((value) -> {
                                if (value) return Text.translatable("options.ycithelper.config.baritone.remove");
                                config.getFishes().remove(pos);
                                AutoConfig.getConfigHolder(ModConfig.class).save();
                                YcithelperClient.nextId = 0;
                                MinecraftClient.getInstance().setScreen(build(parent));
                                return Text.translatable("options.ycithelper.config.baritone.remove");
                            })
                            .build()
            );
        }
    }
}
