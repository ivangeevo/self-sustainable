package org.btwr.self_sustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import org.btwr.core.BTWRMod;
import org.btwr.self_sustainable.SelfSustainableMod;
import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.item.ModItems;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class SelfSustainableLangProvider extends FabricLanguageProvider {

    public SelfSustainableLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder tb) {
        addItemGroup("ss_group", "Self Sustainable!", tb);
        this.addBlockTranslations(tb);
        this.addItemTranslations(tb);
        //this.addConfigTranslations(tb);
        this.addEmiNames(tb);
    }

    private void addBlockTranslations(TranslationBuilder tb) {
        tb.add(ModBlocks.OVEN_BRICK, "Brick Oven");
    }

    private void addItemTranslations(TranslationBuilder tb) {
        tb.add(ModItems.FIRESTARTER_STICKS, "Fire Plough");
        tb.add(ModItems.FIRESTARTER_BOW, "Bow Drill");
        tb.add(ModItems.BRICK_UNFIRED, "Wet Brick");

        tb.add(ModItems.CRUDE_TORCH_UNLIT, "Unlit Crude Torch");
        tb.add(ModItems.CRUDE_TORCH_LIT, "Lit Crude Torch");
        tb.add(ModItems.CRUDE_TORCH_SMOULDER, "Smouldering Crude Torch");
        tb.add(ModItems.CRUDE_TORCH_BURNED_OUT, "Burned Out Crude Torch");
        tb.add(ModItems.TORCH_UNLIT, "Unlit Torch");

        tb.add(ModItems.WICKER, "Wicker");
        tb.add(ModItems.WICKER_WEAVING, "Wicker Weaving");
    }

    private void addConfigTranslations(TranslationBuilder tb) {
        //addConfigMenuTitle("BTWR: Core Configuration Menu", tb);
        //addConfigCategory("general", "General Options", tb);
        //addConfig("knockbackRestriction", "Knockback Restriction", tb);
    }

    protected void addEmiNames(TranslationBuilder tb) {
        addEmiCategory("oven_cooking", "Oven Cooking", tb);
        addEmiCategory("progressive_crafting", "Progressive Crafting", tb);
        addEmiTooltip("progressive_crafting.tooltip", "Hold right click", tb);
    }

    protected void addEmiCategory(String key, String name, TranslationBuilder tb) {
        tb.add("emi.category.self_sustainable." + key, name);
    }

    protected void addEmiTooltip(String key, String name, TranslationBuilder tb) {
        tb.add("emi." + key, name);
    }

    private void addItemGroup(String entryPath, String translation, TranslationBuilder tb) {
        tb.add("itemgroup." + entryPath, translation);
    }

    private void addConfigMenuTitle(String translation, TranslationBuilder tb) {
        tb.add("title." + SelfSustainableMod.MOD_ID + ".config", translation);
    }

    private void addConfigCategory(String categoryPath, String translation, TranslationBuilder tb) {
        tb.add("config." + SelfSustainableMod.MOD_ID + ".category." + categoryPath, translation);
    }

    private void addConfig(String configPath, String translation, TranslationBuilder tb) {
        tb.add("config." + SelfSustainableMod.MOD_ID + "." + configPath, translation);
    }

    private void addConfigTooltip(String configPath, String translation, TranslationBuilder tb) {
        tb.add("config." + SelfSustainableMod.MOD_ID + ".tooltip." + configPath, translation);
    }

}