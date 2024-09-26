package net.ivangeevo.self_sustainable.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.nbt.NbtCompound;

/**
public class TorchFuelFunction extends ConditionalLootFunction {

    protected TorchFuelFunction(LootCondition[] conditions) {
        super(conditions);
    }

    @Override
    public LootFunctionType getType() {
        return SelfSustainableMod.TORCH_FUEL_FUNCTION;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        if (!(stack.getItem() instanceof BlockItem)) return stack; // No regular items

        BlockEntity blockEntity = context.get(LootContextParameters.BLOCK_ENTITY);
        Block block = ((BlockItem) stack.getItem()).getBlock();

        if (block instanceof AbstractModTorchBlock) {

            // Set fuel
            if (blockEntity instanceof TorchBE) {
                int remainingFuel = ((TorchBE) blockEntity).getFuel();

                if (remainingFuel != 48000) {
                    NbtCompound nbt = new NbtCompound();
                    nbt.putInt("Fuel", (remainingFuel));
                    stack.setNbt(nbt);
                }
            }

            if (block instanceof AbstractModTorchBlock && ((AbstractModTorchBlock) ((BlockItem) stack.getItem()).getBlock()).fireState == TorchFireState.BURNED_OUT) {
                stack.removeSubNbt("Fuel");
            }
        }

        return stack;
    }

    public static class Serializer extends ConditionalLootFunction.Serializer<TorchFuelFunction> {

        @Override
        public TorchFuelFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return new TorchFuelFunction(lootConditions);
        }
    }
}
 **/