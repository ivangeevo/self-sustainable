package org.btwr.self_sustainable.datagen.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.DyeColor;
import org.btwr.self_sustainable.SelfSustainableMod;
import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.item.ModItems;
import net.minecraft.registry.RegistryWrapper;
import org.btwr.self_sustainable.tag.ModTags;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SelfSustainableLangProvider extends FabricLanguageProvider {

    public SelfSustainableLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder tb) {
        addItemGroup("ss_group", "Self Sustainable!", tb);
        this.addBlockTranslations(tb);
        this.addItemTranslations(tb);
        this.addSoundTranslations(tb);
        this.addTagTranslations(tb);
        //this.addConfigTranslations(tb);
        this.addEmiTranslations(tb);

        tb.add("container.hamper", "Hamper");
    }

    private void addBlockTranslations(TranslationBuilder tb) {
        tb.add(ModBlocks.OVEN_BRICK, "Brick Oven");
        tb.add(ModBlocks.OVEN_BRICK_MORTARED, "Mortared Brick Oven");
        tb.add(ModBlocks.WICKER_BASKET, "Wicker Basket");
        tb.add(ModBlocks.HAMPER, "Hamper");
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
        tb.add(ModItems.SOUL_TORCH_UNLIT, "Unlit Soul Torch");

        tb.add(ModItems.WICKER, "Wicker");
        tb.add(ModItems.WICKER_WEAVING, "Wicker Weaving");

        tb.add(ModItems.KNITTING_NEEDLES, "Knitting Needles");
        tb.add(ModItems.KNITTING, "Knitting");

        // Translation keys for wool knit/wool items
        for (DyeColor color : DyeColor.values()) {
            String colorName = Arrays.stream(color.getName().split("_"))
                    .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                    .collect(Collectors.joining(" "));

            String woolKnitKey = "item." + SelfSustainableMod.MOD_ID + "." + color.getName() + "_wool_knit";
            tb.add(woolKnitKey, colorName + " Wool Knit");

            String woolKey = "item." + SelfSustainableMod.MOD_ID + "." + color.getName() + "_wool";
            tb.add(woolKey, colorName + " Wool");
        }

        tb.add(ModItems.WOOL_HELMET, "Tuque");
        tb.add(ModItems.WOOL_CHESTPLATE, "Wool Jacket");
        tb.add(ModItems.WOOL_LEGGINGS, "Wool Britches");
        tb.add(ModItems.WOOL_BOOTS, "Bootsies");

        tb.add(ModItems.COOKED_KEBAB, "Cooked Kebab");
        tb.add(ModItems.EGG_SCRAMBLED_COOKED, "Scrambled Eggs");
        tb.add(ModItems.EGG_SCRAMBLED_RAW, "Raw Scrambled Eggs");
        tb.add(ModItems.HAM_AND_EGGS, "Ham and Eggs");
        tb.add(ModItems.HEARTY_STEW, "Hearty Stew");
        tb.add(ModItems.MUSHROOM_OMELETTE_COOKED, "Mushroom Omelette");
        tb.add(ModItems.MUSHROOM_OMELETTE_RAW, "Raw Mushroom Omelette");
        tb.add(ModItems.PORK_DINNER, "Pork Dinner");
        tb.add(ModItems.RAW_KEBAB, "Raw Kebab");
        tb.add(ModItems.SANDWICH, "Tasty Sandwich");
        tb.add(ModItems.STEAK_AND_POTATOES, "Steak and Potatoes");
        tb.add(ModItems.STEAK_DINNER, "Steak Dinner");
        tb.add(ModItems.WOLF_DINNER, "Wolf Dinner");
        tb.add(ModItems.CHICKEN_SOUP, "Chicken Soup");
        tb.add(ModItems.CHOWDER, "Chowder");
    }

    private void addConfigTranslations(TranslationBuilder tb) {
        //addConfigMenuTitle("Self Sustainable Configuration Menu", tb);
        //addConfigCategory("general", "General Options", tb);
        //addConfig("exampleConfigOption", "Example config option", tb);
    }

    private void addSoundTranslations(TranslationBuilder tb) {
        tb.add("subtitles.self_sustainable.primitive_firestarter_use", "Rubbing sticks together");
        tb.add("subtitles.self_sustainable.campfire_ignite", "Campfire ignites");
        tb.add("subtitles.self_sustainable.campfire_burning", "Campfire burning");
        tb.add("subtitles.self_sustainable.oven_ignite", "Oven ignites");
        tb.add("subtitles.self_sustainable.oven_insert_fuel", "Putting fuel in oven");
        tb.add("subtitles.self_sustainable.oven_insert_fuel_active", "Refueling oven");
        tb.add("subtitles.self_sustainable.oven_mortar", "Mortared block");
        tb.add("subtitles.self_sustainable.torch_ignite", "Torch ignites");
        tb.add("subtitles.self_sustainable.torch_smoulder", "Torch smoulders");
        tb.add("subtitles.self_sustainable.torch_extinguish", "Torch extinguishes");
        tb.add("subtitles.self_sustainable.knitting", "Knitting");
        tb.add("subtitles.self_sustainable.knitting_finish", "Wool knit finished");
        tb.add("subtitles.self_sustainable.wicker_weaving", "Weaving wicker");
        tb.add("subtitles.self_sustainable.wicker_weaving_finish", "Wicker finished");
        tb.add("subtitles.self_sustainable.wicker_basket_open", "Wicker basket opens");
        tb.add("subtitles.self_sustainable.wicker_basket_close", "Wicker basket closes");
        tb.add("subtitles.self_sustainable.wicker_basket_insert_item", "Item put into wicker basket");
        tb.add("subtitles.self_sustainable.wicker_basket_take_out_item", "Item taken out of wicker basket");
        tb.add("subtitles.self_sustainable.hamper_open", "Hamper opens");
        tb.add("subtitles.self_sustainable.hamper_close", "Hamper closes");
        tb.add("subtitles.self_sustainable.step_on_wet_brick", "Wet Brick breaks");
        tb.add("subtitles.self_sustainable.wooden_chest_incorrect_break", "Wooden chest shatters");
    }

    private void addTagTranslations(TranslationBuilder tb) {
        addTagName(ModTags.Items.CAMPFIRE_SPITS, "Campfire spits", tb);
        addTagName(ModTags.Items.CAN_START_FIRE_ON_USE, "Can start fire on use", tb);
        addTagName(ModTags.Items.DIRECT_IGNITERS, "Direct Igniters", tb);
        addTagName(ModTags.Items.FIRESTARTERS, "Firestarters", tb);
        addTagName(ModTags.Items.PRIMITIVE_FIRESTARTERS, "Primitive Firestarters", tb);
        addTagName(ModTags.Items.KNITTING_INGREDIENTS, "Knitting Ingredients", tb);
        addTagName(ModTags.Items.LIT_TORCHES, "Lit Torches", tb);
        addTagName(ModTags.Items.UNLIT_TORCHES, "Unlit Torches", tb);
        addTagName(ModTags.Items.WOOL_ITEMS, "Wool Items", tb);
        addTagName(ModTags.Items.WOOL_KNIT_ITEMS, "Wool Knits", tb);
    }

    protected void addEmiTranslations(TranslationBuilder tb) {
        addEmiCategory("oven_cooking", "Oven Cooking", tb);
        addEmiCategory("progressive_crafting", "Progressive Crafting", tb);
        addEmiTooltip("progressive_crafting", "Hold right click", tb);
        addEmiTooltip("brick_sundrying", "§6Leave in the sun for a full day!", tb);
    }

    protected void addEmiCategory(String key, String name, TranslationBuilder tb) {
        tb.add("emi.category.self_sustainable." + key, name);
    }

    protected void addEmiTooltip(String key, String name, TranslationBuilder tb) {
        tb.add("emi." + key + ".tooltip", name);
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

    protected void addTagName(TagKey<?> tagKey, String value, TranslationBuilder tb) {
        tb.add(tagKey, value);
    }

}