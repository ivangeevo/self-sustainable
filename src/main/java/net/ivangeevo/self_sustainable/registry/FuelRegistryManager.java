package net.ivangeevo.self_sustainable.registry;

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
public class FuelRegistryManager
{

    // Register all entries here.
    public static void init()
    {
        modifyEntry(Items.COAL_BLOCK, 14400);
        modifyEntry(Items.BLAZE_ROD, 12800);
        modifyEntry(ItemTags.BAMBOO_BLOCKS, 100);


        removeFuelItems();
        registerFuelItems();

    }

    private static void removeFuelItems()
    {
        // Tags
        FuelRegistry.INSTANCE.remove(ItemTags.LOGS);
        FuelRegistry.INSTANCE.remove(ItemTags.PLANKS);
        FuelRegistry.INSTANCE.remove(ItemTags.WOODEN_STAIRS);

        // Items
        FuelRegistry.INSTANCE.remove(Items.COAL);

    }

    private static void registerFuelItems()
    {
        // Logs
        FuelRegistry.INSTANCE.add(Items.BIRCH_LOG, 16000);
        FuelRegistry.INSTANCE.add(Items.ACACIA_LOG, 16000);
        FuelRegistry.INSTANCE.add(Items.OAK_LOG, 12800);
        FuelRegistry.INSTANCE.add(Items.DARK_OAK_LOG, 12800);
        FuelRegistry.INSTANCE.add(Items.CHERRY_LOG, 12800);
        FuelRegistry.INSTANCE.add(Items.SPRUCE_LOG, 9600);
        FuelRegistry.INSTANCE.add(Items.MANGROVE_LOG, 8400);
        FuelRegistry.INSTANCE.add(Items.JUNGLE_LOG, 6400);
        FuelRegistry.INSTANCE.add(ItemTags.BAMBOO_BLOCKS, 500);

        // Planks
        FuelRegistry.INSTANCE.add(Items.BIRCH_PLANKS, 500);
        FuelRegistry.INSTANCE.add(Items.ACACIA_PLANKS, 500);
        FuelRegistry.INSTANCE.add(Items.OAK_PLANKS, 400);
        FuelRegistry.INSTANCE.add(Items.DARK_OAK_PLANKS, 400);
        FuelRegistry.INSTANCE.add(Items.CHERRY_PLANKS, 400);
        FuelRegistry.INSTANCE.add(Items.SPRUCE_PLANKS, 300);
        FuelRegistry.INSTANCE.add(Items.MANGROVE_PLANKS, 300);
        FuelRegistry.INSTANCE.add(Items.JUNGLE_PLANKS, 100);
        
        // Wooden Stairs
        FuelRegistry.INSTANCE.add(Items.BIRCH_STAIRS, 400);
        FuelRegistry.INSTANCE.add(Items.ACACIA_STAIRS, 400);
        FuelRegistry.INSTANCE.add(Items.OAK_STAIRS, 300);
        FuelRegistry.INSTANCE.add(Items.DARK_OAK_STAIRS, 300);
        FuelRegistry.INSTANCE.add(Items.CHERRY_STAIRS, 300);
        FuelRegistry.INSTANCE.add(Items.SPRUCE_STAIRS, 200);
        FuelRegistry.INSTANCE.add(Items.MANGROVE_STAIRS, 200);
        FuelRegistry.INSTANCE.add(Items.JUNGLE_STAIRS, 70);




    }

    // Additional helper methods to modify items/tags.
    private static void modifyEntry(Item item, int newValue)
    {
        FuelRegistry.INSTANCE.remove(item);
        FuelRegistry.INSTANCE.add(item, newValue);
    }

    private static void modifyEntry(TagKey<Item> tag, int newValue)
    {
        FuelRegistry.INSTANCE.remove(tag);
        FuelRegistry.INSTANCE.add(tag, newValue);
    }


}
