package net.ivangeevo.self_sustainable.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;

/**
public class LootFunctionTorch extends ConditionalLootFunction {

    protected LootFunctionTorch(LootCondition[] conditions) {
        super(conditions);
    }

    @Override
    public LootFunctionType getType() {
        return SelfSustainableMod.TORCH_LOOT_FUNCTION;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        BlockState state = context.get(LootContextParameters.BLOCK_STATE);
        assert state != null;
        ItemStack itemStack = new ItemStack(state.getBlock().asItem());
        TorchFireState torchState;
        TorchFireState dropTorchState;

        // Torch dropping behavior modification
        if (state.getBlock() instanceof AbstractModTorchBlock)
        {
            torchState = ((AbstractModTorchBlock) state.getBlock()).fireState;
            dropTorchState = torchState;

            // Check for smoldering drop
            if (dropTorchState == TorchFireState.SMOULDER)
            {
                dropTorchState = TorchFireState.UNLIT;
            }

            // Check if torches should burn out when dropped
            if (dropTorchState != TorchFireState.BURNED_OUT)
            {
                dropTorchState = TorchFireState.BURNED_OUT;
            }

            itemStack = getChangedStack(state, dropTorchState);

        }


        return itemStack;
    }

    private ItemStack getChangedStack(BlockState state, TorchFireState torchState) {
        return new ItemStack(((AbstractModTorchBlock) state.getBlock()).handler.getStandingTorch(torchState).asItem());
    }

    public static class Serializer extends ConditionalLootFunction.Serializer<LootFunctionTorch> {

        @Override
        public LootFunctionTorch fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return new LootFunctionTorch(lootConditions);
        }
    }
}
 **/