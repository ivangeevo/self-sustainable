package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.block.entity.util.CampfireExtinguisher;
import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockEntityAdded;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlockEntity.class)
public abstract class CampfireBlockEntityMixin extends BlockEntity implements CampfireBlockEntityAdded
{
    @Unique
    private int litTime = 0;

    @Override
    public int getLitTime() {
        return litTime;
    }

    @Override
    public void setLitTime(int value) {
        this.litTime = value;
    }

    public CampfireBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Inject(method = "litServerTick", at = @At("HEAD"))
    private static void injectedExtinguishLogic(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo ci)
    {
        CampfireExtinguisher.onLitServerTick(world, pos, state, campfire);
    }

    @Inject(method = "readNbt", at = @At("RETURN"))
    private void readNbt(NbtCompound nbt, CallbackInfo ci)
    {
        if (nbt.contains("LitTime"))
        {
            litTime = nbt.getInt("LitTime");
        }
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    private void writeNbt(NbtCompound nbt, CallbackInfo ci)
    {
        nbt.putInt("LitTime", litTime);
    }


}
