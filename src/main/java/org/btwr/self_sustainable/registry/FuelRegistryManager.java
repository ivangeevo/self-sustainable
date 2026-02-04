package org.btwr.self_sustainable.registry;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;

/** <p>This class helps modify and remove vanilla fuel items, as well as add new modded ones
 * using the {@link FuelRegistry} interface.
 *
 * <p>See {@link AbstractFurnaceBlockEntity#createFuelTimeMap()} for the original values of fuel items.
 */
public class FuelRegistryManager {

    // Register all entries here.
    public static void initEntries() {
        // First, we try to modify if possible
        modifyEntry(Items.COAL_BLOCK, 14400);
        modifyEntry(Items.BLAZE_ROD, 12800);
        modifyEntry(ItemTags.BAMBOO_BLOCKS, 100);
        modifyEntry(Items.STICK, 400);
        modifyEntry(ItemTags.SAPLINGS, 100);
        modifyEntry(Items.BAMBOO, 15);

        // And then we manually remove and add what is not directly modifiable.
        removeFuelItems();
        addFuelItems();
    }

    private static void removeFuelItems() {
        // Tags
        // we remove tags, because we set a different fuel value for certain wood types.
        FuelRegistry.INSTANCE.remove(ItemTags.LOGS);
        FuelRegistry.INSTANCE.remove(ItemTags.PLANKS);
        FuelRegistry.INSTANCE.remove(ItemTags.WOODEN_STAIRS);
        FuelRegistry.INSTANCE.remove(ItemTags.WOODEN_SLABS);
        FuelRegistry.INSTANCE.remove(ItemTags.WOODEN_TRAPDOORS);
        FuelRegistry.INSTANCE.remove(ItemTags.WOODEN_PRESSURE_PLATES);
        FuelRegistry.INSTANCE.remove(ItemTags.WOODEN_FENCES);
        FuelRegistry.INSTANCE.remove(ItemTags.FENCE_GATES);
        FuelRegistry.INSTANCE.remove(ItemTags.WOODEN_BUTTONS);
        FuelRegistry.INSTANCE.remove(ItemTags.SIGNS);
        FuelRegistry.INSTANCE.remove(ItemTags.HANGING_SIGNS);


        // Items
        FuelRegistry.INSTANCE.remove(Items.COAL);
        FuelRegistry.INSTANCE.remove(Items.CHARCOAL);
    }

    private static void addFuelItems() {
        // Logs & Wood
        FuelRegistry.INSTANCE.add(Items.BIRCH_LOG, 16000);
        FuelRegistry.INSTANCE.add(Items.BIRCH_WOOD, 16000);
        FuelRegistry.INSTANCE.add(Items.ACACIA_LOG, 16000);
        FuelRegistry.INSTANCE.add(Items.ACACIA_WOOD, 16000);
        FuelRegistry.INSTANCE.add(Items.OAK_LOG, 12800);
        FuelRegistry.INSTANCE.add(Items.OAK_WOOD, 12800);
        FuelRegistry.INSTANCE.add(Items.DARK_OAK_LOG, 12800);
        FuelRegistry.INSTANCE.add(Items.DARK_OAK_WOOD, 12800);
        FuelRegistry.INSTANCE.add(Items.CHERRY_LOG, 12800);
        FuelRegistry.INSTANCE.add(Items.CHERRY_WOOD, 12800);
        FuelRegistry.INSTANCE.add(Items.SPRUCE_LOG, 9600);
        FuelRegistry.INSTANCE.add(Items.SPRUCE_WOOD, 9600);
        FuelRegistry.INSTANCE.add(Items.MANGROVE_LOG, 8400);
        FuelRegistry.INSTANCE.add(Items.MANGROVE_WOOD, 8400);
        FuelRegistry.INSTANCE.add(Items.JUNGLE_LOG, 6400);
        FuelRegistry.INSTANCE.add(Items.JUNGLE_WOOD, 6400);

        FuelRegistry.INSTANCE.add(Items.STRIPPED_BIRCH_LOG, 15975);
        FuelRegistry.INSTANCE.add(Items.STRIPPED_BIRCH_WOOD, 15975);
        FuelRegistry.INSTANCE.add(Items.STRIPPED_ACACIA_LOG, 15975);
        FuelRegistry.INSTANCE.add(Items.STRIPPED_ACACIA_WOOD, 15975);
        FuelRegistry.INSTANCE.add(Items.STRIPPED_OAK_LOG, 12775);
        FuelRegistry.INSTANCE.add(Items.STRIPPED_OAK_WOOD, 12775);
        FuelRegistry.INSTANCE.add(Items.STRIPPED_DARK_OAK_LOG, 12775);
        FuelRegistry.INSTANCE.add(Items.STRIPPED_DARK_OAK_WOOD, 12775);
        FuelRegistry.INSTANCE.add(Items.STRIPPED_CHERRY_LOG, 12775);
        FuelRegistry.INSTANCE.add(Items.STRIPPED_CHERRY_WOOD, 12775);
        FuelRegistry.INSTANCE.add(Items.STRIPPED_SPRUCE_LOG, 9757);
        FuelRegistry.INSTANCE.add(Items.STRIPPED_SPRUCE_WOOD, 9757);
        FuelRegistry.INSTANCE.add(Items.STRIPPED_MANGROVE_LOG, 8375);
        FuelRegistry.INSTANCE.add(Items.STRIPPED_MANGROVE_WOOD, 8375);
        FuelRegistry.INSTANCE.add(Items.STRIPPED_JUNGLE_LOG, 6375);
        FuelRegistry.INSTANCE.add(Items.STRIPPED_JUNGLE_WOOD, 6375);

        FuelRegistry.INSTANCE.add(ItemTags.WARPED_STEMS, 1500);
        FuelRegistry.INSTANCE.add(ItemTags.CRIMSON_STEMS, 1500);

        // Planks
        FuelRegistry.INSTANCE.add(Items.BIRCH_PLANKS, 4000);
        FuelRegistry.INSTANCE.add(Items.ACACIA_PLANKS, 4000);
        FuelRegistry.INSTANCE.add(Items.OAK_PLANKS, 3200);
        FuelRegistry.INSTANCE.add(Items.DARK_OAK_PLANKS, 3200);
        FuelRegistry.INSTANCE.add(Items.CHERRY_PLANKS, 2400);
        FuelRegistry.INSTANCE.add(Items.SPRUCE_PLANKS, 2400);
        FuelRegistry.INSTANCE.add(Items.MANGROVE_PLANKS, 1600);
        FuelRegistry.INSTANCE.add(Items.JUNGLE_PLANKS, 1600);
        //FuelRegistry.INSTANCE.add(Items.BAMBOO_PLANKS, 130);

        // Wooden Stairs
        FuelRegistry.INSTANCE.add(Items.BIRCH_STAIRS, 3000);
        FuelRegistry.INSTANCE.add(Items.ACACIA_STAIRS, 3000);
        FuelRegistry.INSTANCE.add(Items.OAK_STAIRS, 2200);
        FuelRegistry.INSTANCE.add(Items.DARK_OAK_STAIRS, 2200);
        FuelRegistry.INSTANCE.add(Items.CHERRY_STAIRS, 1400);
        FuelRegistry.INSTANCE.add(Items.SPRUCE_STAIRS, 1400);
        FuelRegistry.INSTANCE.add(Items.MANGROVE_STAIRS, 600);
        FuelRegistry.INSTANCE.add(Items.JUNGLE_STAIRS, 600);

        //FuelRegistry.INSTANCE.add(Items.BAMBOO_STAIRS, 100);

        //FuelRegistry.INSTANCE.add(Blocks.BAMBOO_MOSAIC_STAIRS, 150);

        // Wooden Slabs
        FuelRegistry.INSTANCE.add(Items.BIRCH_SLAB, 2000);
        FuelRegistry.INSTANCE.add(Items.ACACIA_SLAB, 2000);
        FuelRegistry.INSTANCE.add(Items.OAK_SLAB, 1100);
        FuelRegistry.INSTANCE.add(Items.DARK_OAK_SLAB, 1100);
        FuelRegistry.INSTANCE.add(Items.CHERRY_SLAB, 700);
        FuelRegistry.INSTANCE.add(Items.SPRUCE_SLAB, 700);
        FuelRegistry.INSTANCE.add(Items.MANGROVE_SLAB, 300);
        FuelRegistry.INSTANCE.add(Items.JUNGLE_SLAB, 300);
        //FuelRegistry.INSTANCE.add(Items.BAMBOO_SLAB, 75);

        //FuelRegistry.INSTANCE.add(Blocks.BAMBOO_MOSAIC_SLAB, 150);

        //TODO: Re-balance fuel values. All values below this one haven't been changed fully

        // Wooden Trapdoors
        FuelRegistry.INSTANCE.add(Items.BIRCH_TRAPDOOR, 1000);
        FuelRegistry.INSTANCE.add(Items.ACACIA_TRAPDOOR, 1000);
        FuelRegistry.INSTANCE.add(Items.OAK_TRAPDOOR, 1000);
        FuelRegistry.INSTANCE.add(Items.DARK_OAK_TRAPDOOR, 225);
        FuelRegistry.INSTANCE.add(Items.CHERRY_TRAPDOOR, 225);
        FuelRegistry.INSTANCE.add(Items.SPRUCE_TRAPDOOR, 175);
        FuelRegistry.INSTANCE.add(Items.MANGROVE_TRAPDOOR, 175);
        FuelRegistry.INSTANCE.add(Items.JUNGLE_TRAPDOOR, 125);

        //FuelRegistry.INSTANCE.add(Items.BAMBOO_TRAPDOOR, 100);

        // Wooden Pressure Plates
        FuelRegistry.INSTANCE.add(Items.BIRCH_PRESSURE_PLATE, 125);
        FuelRegistry.INSTANCE.add(Items.ACACIA_PRESSURE_PLATE, 125);
        FuelRegistry.INSTANCE.add(Items.OAK_PRESSURE_PLATE, 100);
        FuelRegistry.INSTANCE.add(Items.DARK_OAK_PRESSURE_PLATE, 100);
        FuelRegistry.INSTANCE.add(Items.CHERRY_PRESSURE_PLATE, 100);
        FuelRegistry.INSTANCE.add(Items.SPRUCE_PRESSURE_PLATE, 75);
        FuelRegistry.INSTANCE.add(Items.MANGROVE_PRESSURE_PLATE, 75);
        FuelRegistry.INSTANCE.add(Items.JUNGLE_PRESSURE_PLATE, 50);

        //FuelRegistry.INSTANCE.add(Items.BAMBOO_PRESSURE_PLATE, 50);

        // Wooden Fences
        FuelRegistry.INSTANCE.add(Items.BIRCH_FENCE, 275);
        FuelRegistry.INSTANCE.add(Items.ACACIA_FENCE, 275);
        FuelRegistry.INSTANCE.add(Items.OAK_FENCE, 225);
        FuelRegistry.INSTANCE.add(Items.DARK_OAK_FENCE, 225);
        FuelRegistry.INSTANCE.add(Items.CHERRY_FENCE, 225);
        FuelRegistry.INSTANCE.add(Items.SPRUCE_FENCE, 175);
        FuelRegistry.INSTANCE.add(Items.MANGROVE_FENCE, 175);
        FuelRegistry.INSTANCE.add(Items.JUNGLE_FENCE, 125);

        // Wooden Fence Gates
        FuelRegistry.INSTANCE.add(Items.BIRCH_FENCE_GATE, 275);
        FuelRegistry.INSTANCE.add(Items.ACACIA_FENCE_GATE, 275);
        FuelRegistry.INSTANCE.add(Items.OAK_FENCE_GATE, 225);
        FuelRegistry.INSTANCE.add(Items.DARK_OAK_FENCE_GATE, 225);
        FuelRegistry.INSTANCE.add(Items.CHERRY_FENCE_GATE, 225);
        FuelRegistry.INSTANCE.add(Items.SPRUCE_FENCE_GATE, 175);
        FuelRegistry.INSTANCE.add(Items.MANGROVE_FENCE_GATE, 175);
        FuelRegistry.INSTANCE.add(Items.JUNGLE_FENCE_GATE, 125);

        // Wooden Buttons
        FuelRegistry.INSTANCE.add(Items.BIRCH_BUTTON, 65);
        FuelRegistry.INSTANCE.add(Items.ACACIA_BUTTON, 65);
        FuelRegistry.INSTANCE.add(Items.OAK_BUTTON, 50);
        FuelRegistry.INSTANCE.add(Items.DARK_OAK_BUTTON, 50);
        FuelRegistry.INSTANCE.add(Items.CHERRY_BUTTON, 50);
        FuelRegistry.INSTANCE.add(Items.SPRUCE_BUTTON, 40);
        FuelRegistry.INSTANCE.add(Items.MANGROVE_BUTTON, 40);
        FuelRegistry.INSTANCE.add(Items.JUNGLE_BUTTON, 25);

        // Signs
        FuelRegistry.INSTANCE.add(Items.BIRCH_SIGN, 275);
        FuelRegistry.INSTANCE.add(Items.ACACIA_SIGN, 275);
        FuelRegistry.INSTANCE.add(Items.OAK_SIGN, 225);
        FuelRegistry.INSTANCE.add(Items.DARK_OAK_SIGN, 225);
        FuelRegistry.INSTANCE.add(Items.CHERRY_SIGN, 225);
        FuelRegistry.INSTANCE.add(Items.SPRUCE_SIGN, 175);
        FuelRegistry.INSTANCE.add(Items.MANGROVE_SIGN, 175);
        FuelRegistry.INSTANCE.add(Items.JUNGLE_SIGN, 125);

        // Hanging Signs
        FuelRegistry.INSTANCE.add(Items.BIRCH_HANGING_SIGN, 275);
        FuelRegistry.INSTANCE.add(Items.ACACIA_HANGING_SIGN, 275);
        FuelRegistry.INSTANCE.add(Items.OAK_HANGING_SIGN, 225);
        FuelRegistry.INSTANCE.add(Items.DARK_OAK_HANGING_SIGN, 225);
        FuelRegistry.INSTANCE.add(Items.CHERRY_HANGING_SIGN, 225);
        FuelRegistry.INSTANCE.add(Items.SPRUCE_HANGING_SIGN, 175);
        FuelRegistry.INSTANCE.add(Items.MANGROVE_HANGING_SIGN, 175);
        FuelRegistry.INSTANCE.add(Items.JUNGLE_HANGING_SIGN, 125);

        // New added fuel items
        FuelRegistry.INSTANCE.add(Items.FEATHER, 15);
        FuelRegistry.INSTANCE.add(Items.SUGAR_CANE, 20);

        FuelRegistry.INSTANCE.add(ItemTags.SMALL_FLOWERS, 100);
        FuelRegistry.INSTANCE.add(ItemTags.TALL_FLOWERS, 200);

    }

    // Helper methods to modify items/tags.
    private static void modifyEntry(Item item, int newValue) {
        FuelRegistry.INSTANCE.remove(item);
        FuelRegistry.INSTANCE.add(item, newValue);
    }

    private static void modifyEntry(TagKey<Item> tag, int newValue) {
        FuelRegistry.INSTANCE.remove(tag);
        FuelRegistry.INSTANCE.add(tag, newValue);
    }

}