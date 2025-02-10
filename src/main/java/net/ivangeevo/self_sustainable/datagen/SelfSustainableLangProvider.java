package net.ivangeevo.self_sustainable.datagen;

import btwr.core.BTWRMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.item.ModItems;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class SelfSustainableLangProvider extends FabricLanguageProvider {


    public SelfSustainableLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder tb) {
        addItemGroup(SelfSustainableMod.MOD_ID, "Self Sustainable!", tb);
        this.addBlockTranslations(tb);
        this.addItemTranslations(tb);
        this.addConfigTranslations(tb);
    }

    private void addBlockTranslations(TranslationBuilder tb) {
        //tb.add();
    }

    private void addItemTranslations(TranslationBuilder tb) {
        tb.add(ModItems.FIRESTARTER_STICKS, "Fire Plough");
        tb.add(ModItems.FIRESTARTER_BOW, "Bow Drill");
        tb.add(ModItems.BRICK_UNFIRED, "Wet Brick");
    }

    private void addConfigTranslations(TranslationBuilder tb) {
        //addConfigMenuTitle("BTWR: Core Configuration Menu", tb);
        //addConfigCategory("general", "General Options", tb);
        //addConfig("knockbackRestriction", "Knockback Restriction", tb);

    }

    private void addItemGroup(String entryPath, String translation, TranslationBuilder tb) {
        tb.add("itemgroup." + entryPath, translation);
    }

    private void addConfigMenuTitle(String translation, TranslationBuilder tb) {
        tb.add("title." + BTWRMod.MOD_ID + ".config", translation);
    }

    private void addConfigCategory(String categoryPath, String translation, TranslationBuilder tb) {
        tb.add("config." + BTWRMod.MOD_ID + ".category." + categoryPath, translation);
    }

    private void addConfig(String configPath, String translation, TranslationBuilder tb) {
        tb.add("config." + BTWRMod.MOD_ID + "." + configPath, translation);
    }

    private void addConfigTooltip(String configPath, String translation, TranslationBuilder tb) {
        tb.add("config." + BTWRMod.MOD_ID + ".tooltip." + configPath, translation);
    }
}
