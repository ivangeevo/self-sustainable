package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin
{

    /** keep this for reference if I need to change entities later.
    // Do not make static
    //@ModifyVariable(method = "<init>", at = @At(value = "LOAD"), argsOnly = true)
    protected BlockEntityType<?> changeType(BlockEntityType<?> type)
    {
        if (type == BlockEntityType.CAMPFIRE)
        {
            return ModBlockEntities.PRIMITIVE_CAMPFIRE;
        }
        else
        {
            return type;
        }
    }
     **/

}
