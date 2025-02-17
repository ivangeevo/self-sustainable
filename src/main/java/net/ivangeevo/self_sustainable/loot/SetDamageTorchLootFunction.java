package net.ivangeevo.self_sustainable.loot;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;

import net.ivangeevo.self_sustainable.block.entity.TorchBE;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

public class SetDamageTorchLootFunction extends ConditionalLootFunction
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<SetDamageTorchLootFunction> CODEC = RecordCodecBuilder.mapCodec(
            (instance) -> addConditionsField(instance)
                    .apply(instance, SetDamageTorchLootFunction::new));

    private SetDamageTorchLootFunction(List<LootCondition> conditions) {
        super(conditions);
    }

    @Override
    public LootFunctionType<SetDamageTorchLootFunction> getType() {
        return ModLootFunctionTypes.SET_DAMAGE_TORCH;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return Set.of(); // No required parameters, as we’re using block entity data directly
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        if (stack.isDamageable()) {
            BlockEntity blockEntity = context.get(LootContextParameters.BLOCK_ENTITY);
            if (blockEntity != null) {
                int customDamageValue = getCustomDamageFromBlockEntity(blockEntity);
                stack.setDamage(customDamageValue);
            } else {
                LOGGER.warn("Block entity not found in loot context for item {}", stack);
            }
        } else {
            LOGGER.warn("Couldn't set damage of loot item {}", stack);
        }

        return stack;
    }

    private int getCustomDamageFromBlockEntity(BlockEntity blockEntity) {
        if (!(blockEntity instanceof TorchBE be)) {
            return 0;
        }

        return be.getFuel();
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return builder(SetDamageTorchLootFunction::new);
    }
}
