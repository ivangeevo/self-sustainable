package net.ivangeevo.self_sustainable.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.List;

public class SettingsGUI
{
    static SSSettings settingsCommon = SelfSustainableMod.getInstance().settings;
    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent).setTitle(Text.translatable("title.self_sustainable.config"));
        builder.setSavingRunnable(() -> {
            SelfSustainableMod.getInstance().saveSettings();


        });

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




    public static Text[] wrapLines(Text text){
        List<StringVisitable> lines = MinecraftClient.getInstance().textRenderer.getTextHandler()
                .wrapLines(text,Math.max(MinecraftClient.getInstance().getWindow().getScaledWidth()/2 - 43,170),
                        Style.EMPTY);
        lines.get(0).getString();
        Text[] textLines = new Text[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            textLines[i]=Text.literal(lines.get(i).getString());
        }
        return textLines;
    }



}
