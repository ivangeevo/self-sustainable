package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.item.interfaces.ItemStackAdded;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackAdded
{
    @Shadow public abstract boolean hasNbt();
    @Shadow @Nullable private NbtCompound nbt;

    @Override
    public long getTimeOfLastUse()
    {
        if ( hasNbt() && this.nbt.contains( "fcLastUse" ) )
        {
            return nbt.getLong( "fcLastUse" );
        }

        return -1;
    }
    @Override
    public void setTimeOfLastUse(long lTime)
    {
        if ( !hasNbt() )
        {
            nbt = new NbtCompound();
        }

        nbt.putLong( "fcLastUse", lTime );
    }
    @Override
    public float getAccumulatedChance(float fDefault)
    {
        if ( hasNbt() && this.nbt.contains( "fcChance" ) )
        {
            return nbt.getFloat( "fcChance" );
        }

        return fDefault;
    }
    @Override
    public void setAccumulatedChance(float fChance)
    {
        if ( !hasNbt() )
        {
            nbt = new NbtCompound( );
        }

        nbt.putFloat( "fcChance", fChance );
    }
}
