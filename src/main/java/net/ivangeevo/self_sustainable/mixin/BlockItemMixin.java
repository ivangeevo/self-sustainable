package net.ivangeevo.self_sustainable.mixin;

import com.terraformersmc.modmenu.util.mod.Mod;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.item.ModItems;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item implements Ignitable
{
    @Shadow public abstract ActionResult place(ItemPlacementContext context);

    @Shadow public abstract ActionResult useOnBlock(ItemUsageContext context);

    public BlockItemMixin(Settings settings) {
        super(settings);
    }

    // keep for reference on how to change what block is placed from an item
    /**
     // Injected logic for replacing vanilla's placed block torch with the modded one, so we can utilize all block entity capabilities.
     @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", at = @At("HEAD"), cancellable = true)
     private void injectedPlace(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir)
     {
     BlockPos pos = context.getBlockPos();
     BlockState torchState = Blocks.DIRT.getDefaultState();
     BlockState wallTorchState = Blocks.WALL_TORCH.getStateWithProperties(state);

     boolean setModdedTorchState = context.getWorld().setBlockState(pos, torchState,
     Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);

     if (context.getStack().isOf(Items.CAMPFIRE))
     {
     cir.setReturnValue(setModdedTorchState);
     }



     }
     **/


    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void injectedOnUse(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir)
    {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();

        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        ItemStack heldStack = context.getStack();

        if (heldStack.isIn(ModTags.Items.CAN_START_FIRE_ON_USE))
        {

            // Check if the block at the position is a campfire or oven and try lighting up.
            if (block == Blocks.CAMPFIRE
                    || (block == ModBlocks.OVEN_BRICK && heldStack.getItem() != ModBlocks.OVEN_BRICK.asItem())) {
                if (world.canPlayerModifyAt(player, pos)) {
                    if (!world.isClient) {
                        attemptToLightBlock(context.getStack(), world, pos, context.getSide());
                    }

                    cir.setReturnValue(ActionResult.SUCCESS);
                } else {
                    cir.setReturnValue(ActionResult.FAIL);
                }
            } else {
                // Default logic for placing or using the item
                ActionResult actionResult = this.place(new ItemPlacementContext(context));
                if (!actionResult.isAccepted() && context.getStack().contains(DataComponentTypes.FOOD)) {
                    ActionResult actionResult2 = this.use(context.getWorld(), context.getPlayer(), context.getHand()).getResult();
                    cir.setReturnValue(actionResult2 == ActionResult.CONSUME ? ActionResult.CONSUME_PARTIAL : actionResult2);
                }
                cir.setReturnValue(actionResult);
            }
        }
    }

    @Override
    public boolean attemptToLightBlock(ItemStack stack, World world, BlockPos pos, Direction facing)
    {
        Block targetBlock = world.getBlockState(pos).getBlock();

        if ( targetBlock != null && targetBlock.getCanBeSetOnFireDirectlyByItem(world, pos) )
        {
            return targetBlock.setOnFireDirectly(world, pos);
        }

        return false;
    }

}
