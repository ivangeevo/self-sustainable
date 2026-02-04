package org.btwr.self_sustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import org.btwr.self_sustainable.item.ModItems;
import org.btwr.self_sustainable.tag.ModTags;
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
        getOrCreateTagBuilder(ModTags.Items.LIT_TORCHES)
                .add(Items.TORCH)
                .add(Items.SOUL_TORCH)
                .add(ModItems.CRUDE_TORCH_LIT)
                .add(ModItems.CRUDE_TORCH_SMOULDER);

        getOrCreateTagBuilder(ModTags.Items.UNLIT_TORCHES)
                .add(ModItems.TORCH_UNLIT)
                .add(ModItems.SOUL_TORCH_UNLIT)
                .add(ModItems.CRUDE_TORCH_UNLIT);

        getOrCreateTagBuilder(ModTags.Items.PRIMITIVE_FIRESTARTERS)
                .add(ModItems.FIRESTARTER_STICKS)
                .add(ModItems.FIRESTARTER_BOW);

        getOrCreateTagBuilder(ModTags.Items.FIRESTARTERS)
                .addTag(ModTags.Items.PRIMITIVE_FIRESTARTERS)
                .add(Items.FLINT_AND_STEEL);

        getOrCreateTagBuilder(ModTags.Items.DIRECT_IGNITERS)
                .addTag(ModTags.Items.LIT_TORCHES)
                .add(Items.FIRE_CHARGE);

        getOrCreateTagBuilder(ModTags.Items.CAN_START_FIRE_ON_USE)
                .addTag(ModTags.Items.FIRESTARTERS)
                .addTag(ModTags.Items.DIRECT_IGNITERS);

        getOrCreateTagBuilder(ModTags.Items.CAMPFIRE_SPITS)
                .add(Items.STICK);
    }

}