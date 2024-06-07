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



        getOrCreateTagBuilder(ModTags.Items.PRIMITIVE_FIRESTARTERS)
            .add(ModItems.FIRESTARTER_STICKS)
            .add(ModItems.FIRESTARTER_BOW);

        getOrCreateTagBuilder(ModTags.Items.DIRECT_IGNITERS)
                .add(Items.TORCH)
                .add(Items.SOUL_TORCH);



    }
}
