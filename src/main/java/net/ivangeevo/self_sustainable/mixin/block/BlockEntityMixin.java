package net.ivangeevo.self_sustainable.mixin.block;

import net.minecraft.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

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
