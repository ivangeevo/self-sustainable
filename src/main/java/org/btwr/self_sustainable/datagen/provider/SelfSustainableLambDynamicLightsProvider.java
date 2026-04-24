package org.btwr.self_sustainable.datagen.provider;

import dev.lambdaurora.lambdynlights.api.data.ItemLightSourceDataProvider;
import dev.lambdaurora.lambdynlights.api.item.ItemLuminance;
import net.minecraft.data.DataOutput;
import net.minecraft.registry.RegistryWrapper;
import org.btwr.self_sustainable.SelfSustainableMod;
import org.btwr.self_sustainable.block.blocks.CrudeTorchBlock;
import org.btwr.self_sustainable.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class SelfSustainableLambDynamicLightsProvider extends ItemLightSourceDataProvider {

    public static SelfSustainableLambDynamicLightsProvider register(DataOutput packOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryProvider) {
       return new SelfSustainableLambDynamicLightsProvider(packOutput, registryProvider, SelfSustainableMod.MOD_ID);
    }

    public SelfSustainableLambDynamicLightsProvider(DataOutput packOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryProvider, String defaultNamespace) {
        super(packOutput, registryProvider, defaultNamespace);
    }

    @Override
    protected void generate(Context context) {
        context.add(ModItems.CRUDE_TORCH_LIT, ItemLuminance.of(CrudeTorchBlock.LIT_LUMINANCE), false);
        context.add(ModItems.CRUDE_TORCH_SMOULDER, ItemLuminance.of(CrudeTorchBlock.SMOULDERING_LUMINANCE), false);
    }

}