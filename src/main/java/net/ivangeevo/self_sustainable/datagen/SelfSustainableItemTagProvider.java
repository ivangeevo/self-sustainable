package net.ivangeevo.self_sustainable.datagen;

import com.terraformersmc.modmenu.util.mod.Mod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.ivangeevo.self_sustainable.item.ModItems;
import net.ivangeevo.self_sustainable.tag.BTWRConventionalTags;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class SelfSustainableItemTagProvider extends FabricTagProvider.ItemTagProvider {


    public SelfSustainableItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg)
    {

        getOrCreateTagBuilder(BTWRConventionalTags.Items.SPIT_CAMPFIRE_ITEMS)
                .add(Items.STICK);

        getOrCreateTagBuilder(BTWRConventionalTags.Items.TORCHES_CAN_IGNITE)
                .add(Items.TORCH);

        this.addToModTags();





    }

    private void addToModTags()
    {
        getOrCreateTagBuilder(ModTags.Items.CAN_START_FIRE_ON_USE)
                .addTag(ModTags.Items.PRIMITIVE_FIRESTARTERS)
                .addTag(BTWRConventionalTags.Items.TORCHES_CAN_IGNITE)
                .add(Items.FLINT_AND_STEEL)
                .add(Items.TORCH);

        getOrCreateTagBuilder(ModTags.Items.CAN_BE_SET_ON_FIRE_ON_USE)
                .add(ModItems.TORCH_UNLIT);

        getOrCreateTagBuilder(ModTags.Items.PRIMITIVE_FIRESTARTERS)
                .add(ModItems.FIRESTARTER_STICKS)
                .add(ModItems.FIRESTARTER_BOW);

        getOrCreateTagBuilder(ModTags.Items.DIRECT_IGNITERS)
                .add(Items.TORCH)
                .add(Items.SOUL_TORCH);
    }
}
