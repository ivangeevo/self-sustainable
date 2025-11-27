package net.ivangeevo.self_sustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.ivangeevo.self_sustainable.item.ModItems;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class SelfSustainableItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public SelfSustainableItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        this.addToVanillaTags();
        this.addToConventionalTags();
        this.addToModTags();
    }

    private void addToVanillaTags() {

    }

    private void addToConventionalTags() {

    }

    private void addToModTags() {
        getOrCreateTagBuilder(ModTags.Items.TORCH_EXTINGUISHERS)
                .forceAddTag(ItemTags.SHOVELS);

        getOrCreateTagBuilder(ModTags.Items.CAN_START_FIRE_ON_USE)
                .addTag(ModTags.Items.PRIMITIVE_FIRESTARTERS)
                .addTag(ModTags.Items.DIRECT_IGNITERS)
                .add(Items.FLINT_AND_STEEL);

        getOrCreateTagBuilder(ModTags.Items.PRIMITIVE_FIRESTARTERS)
                .add(ModItems.FIRESTARTER_STICKS)
                .add(ModItems.FIRESTARTER_BOW);

        getOrCreateTagBuilder(ModTags.Items.DIRECT_IGNITERS)
                .add(Items.TORCH)
                .add(Items.SOUL_TORCH)
                .add(ModItems.CRUDE_TORCH_LIT)
                .add(ModItems.CRUDE_TORCH_SMOULDER)
                .add(Items.FIRE_CHARGE);
    }

}