package net.ivangeevo.self_sustainable.mixin;

import com.google.common.collect.Maps;
import net.minecraft.SharedConstants;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin
{
    // Method is also invoke modified by Fabric, so keep that in mind for incompatibilities later on.
    @Inject(method = "getFuelTime", at = @At("HEAD"), cancellable = true)
    private void injectedFuelTime(ItemStack fuel, CallbackInfoReturnable<Integer> cir)
    {
        if (fuel.isEmpty()) {
            cir.setReturnValue(0);
        } else {
            Item item = fuel.getItem();
            cir.setReturnValue(createNewFuelTimeMap().getOrDefault(item, 0));
        }
    }


    // Copied and modified fuel map from vanilla. Used IdentityHashMap instead of LinkedMap.
    @Unique
    private static Map<Item, Integer> createNewFuelTimeMap() {
        Map<Item, Integer> map = Maps.newIdentityHashMap();
        addFuel(map,  Items.LAVA_BUCKET, 20000);
        addFuel(map,  Blocks.COAL_BLOCK, 16000);
        addFuel(map, Items.BLAZE_ROD, 2400);
        addFuel(map, Items.COAL, 1600);
        addFuel(map, Items.CHARCOAL, 1600);
        //addFuel(map,  ItemTags.LOGS, 15000);

        addFuel(map, Items.BIRCH_LOG, 16000);
        addFuel(map, Items.ACACIA_LOG, 16000);

        addFuel(map, Items.OAK_LOG, 12800);
        addFuel(map, Items.DARK_OAK_LOG, 12800);
        addFuel(map, Items.CHERRY_LOG, 12800);

        addFuel(map, Items.SPRUCE_LOG, 9600);
        addFuel(map, Items.MANGROVE_LOG, 8400);
        addFuel(map, Items.JUNGLE_LOG, 6400);


        addFuel(map, ItemTags.BAMBOO_BLOCKS, 300);

        //addFuel(map, ItemTags.PLANKS, 300);

        addFuel(map, Items.BIRCH_PLANKS, 500);
        addFuel(map, Items.ACACIA_PLANKS, 500);

        addFuel(map, Items.OAK_PLANKS, 400);
        addFuel(map, Items.DARK_OAK_PLANKS, 400);
        addFuel(map, Items.CHERRY_PLANKS, 400);

        addFuel(map, Items.SPRUCE_PLANKS, 300);
        addFuel(map, Items.MANGROVE_PLANKS, 300);
        addFuel(map, Items.JUNGLE_PLANKS, 200);

        addFuel(map, Blocks.BAMBOO_MOSAIC, 300);
        addFuel(map, ItemTags.WOODEN_STAIRS, 300);
        addFuel(map, Blocks.BAMBOO_MOSAIC_STAIRS, 300);
        addFuel(map, ItemTags.WOODEN_SLABS, 150);
        addFuel(map, Blocks.BAMBOO_MOSAIC_SLAB, 150);
        addFuel(map, ItemTags.WOODEN_TRAPDOORS, 300);
        addFuel(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        addFuel(map, ItemTags.WOODEN_FENCES, 300);
        addFuel(map, ItemTags.FENCE_GATES, 300);
        addFuel(map, Blocks.NOTE_BLOCK, 300);
        addFuel(map, Blocks.BOOKSHELF, 300);
        addFuel(map, Blocks.CHISELED_BOOKSHELF, 300);
        addFuel(map, Blocks.LECTERN, 300);
        addFuel(map, Blocks.JUKEBOX, 300);
        addFuel(map, Blocks.CHEST, 300);
        addFuel(map, Blocks.TRAPPED_CHEST, 300);
        addFuel(map, Blocks.CRAFTING_TABLE, 300);
        addFuel(map, Blocks.DAYLIGHT_DETECTOR, 300);
        addFuel(map, ItemTags.BANNERS, 300);
        addFuel(map, Items.BOW, 300);
        addFuel(map, Items.FISHING_ROD, 300);
        addFuel(map, Blocks.LADDER, 300);
        addFuel(map, ItemTags.SIGNS, 200);
        addFuel(map, ItemTags.HANGING_SIGNS, 800);
        addFuel(map, Items.WOODEN_SHOVEL, 200);
        addFuel(map, Items.WOODEN_SWORD, 200);
        addFuel(map, Items.WOODEN_HOE, 200);
        addFuel(map, Items.WOODEN_AXE, 200);
        addFuel(map, Items.WOODEN_PICKAXE, 200);
        addFuel(map, ItemTags.WOODEN_DOORS, 200);
        addFuel(map, ItemTags.BOATS, 1200);
        addFuel(map, ItemTags.WOOL, 100);
        addFuel(map, ItemTags.WOODEN_BUTTONS, 100);
        addFuel(map, Items.STICK, 50);
        addFuel(map, ItemTags.SAPLINGS, 100);
        addFuel(map, Items.BOWL, 100);
        addFuel(map, ItemTags.WOOL_CARPETS, 67);
        addFuel(map, Blocks.DRIED_KELP_BLOCK, 4001);
        addFuel(map, Items.CROSSBOW, 300);
        addFuel(map, Blocks.BAMBOO, 50);
        addFuel(map, Blocks.DEAD_BUSH, 100);
        addFuel(map, Blocks.SCAFFOLDING, 50);
        addFuel(map, Blocks.LOOM, 300);
        addFuel(map, Blocks.BARREL, 300);
        addFuel(map, Blocks.CARTOGRAPHY_TABLE, 300);
        addFuel(map, Blocks.FLETCHING_TABLE, 300);
        addFuel(map, Blocks.SMITHING_TABLE, 300);
        addFuel(map, Blocks.COMPOSTER, 300);
        addFuel(map, Blocks.AZALEA, 100);
        addFuel(map, Blocks.FLOWERING_AZALEA, 100);
        addFuel(map, Blocks.MANGROVE_ROOTS, 300);
        return map;
    }

    @Unique
    private static void addFuel(Map<Item, Integer> fuelTimes, ItemConvertible item, int fuelTime) {
        Item item2 = item.asItem();
        if (isNonFlammableWood(item2)) {
            if (SharedConstants.isDevelopment) {
                throw Util.throwOrPause(new IllegalStateException("A developer tried to explicitly make fire resistant item " + item2.getName(null).getString() + " a furnace fuel. That will not work!"));
            }
        } else {
            fuelTimes.put(item2, fuelTime);
        }
    }

    @Unique
    private static void addFuel(Map<Item, Integer> fuelTimes, TagKey<Item> tag, int fuelTime) {

        for (RegistryEntry<Item> registryEntry : Registries.ITEM.iterateEntries(tag)) {
            if (!isNonFlammableWood(registryEntry.value())) {
                fuelTimes.put(registryEntry.value(), fuelTime);
            }
        }

    }

    @Unique
    private static boolean isNonFlammableWood(Item item) {
        return item.getRegistryEntry().isIn(ItemTags.NON_FLAMMABLE_WOOD);
    }

}
