package net.ivangeevo.self_sustainable.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class SettingsGUI
{
    static SSSettings settingsCommon = SelfSustainableMod.getInstance().settings;
    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent).setTitle(Text.translatable("title.self_sustainable.config"));
        builder.setSavingRunnable(() -> SelfSustainableMod.getInstance().saveSettings());

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.self_sustainable.category.general"));

        /** General Category**/
        general.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.translatable("config.self_sustainable.hcPlayerMiningSpeed"), settingsCommon.ovenNoGUI)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> settingsCommon.ovenNoGUI=newValue)
                .build());


        return builder.build();
    }

}
