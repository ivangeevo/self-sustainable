package net.ivangeevo.self_sustainable.item.items;

import com.terraformersmc.modmenu.util.mod.Mod;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.block.interfaces.TorchBlockAdded;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Objects;

import static net.minecraft.state.property.Properties.LIT;

/** A regular torch item. Gets put out only if submerged in water. **/
public class TorchItem extends VerticallyAttachableBlockItem implements Ignitable
{
    public TorchItem(Block standingBlock, Block wallBlock, Settings settings, Direction verticalAttachmentDirection)
    {
        super(standingBlock, wallBlock, settings, verticalAttachmentDirection);
    }

    /**
    @Override
    public ActionResult useOnBlock(ItemUsageContext context)
    {
        BlockPos pos = context.getBlockPos();
        BlockState state = context.getWorld().getBlockState(pos);

        boolean isStandingBlock = state.isOf(ModBlocks.TORCH.getStateWithProperties(state.with(LIT, false)).getBlock());
        boolean isWallBlock = wallBlock.getStateManager().getDefaultState().isOf(state.with(LIT, false).getBlock());

        if ( isStandingBlock )
        {
            context.getWorld().setBlockState(pos, ModBlocks.TORCH.getStateWithProperties(state.with(LIT,true)), 3);
            playLitFX(context.getWorld(), pos);
            return ActionResult.SUCCESS;
        }
        else if (isWallBlock)
        {
            context.getWorld().setBlockState(pos, ModBlocks.WALL_TORCH.getStateWithProperties(state.with(LIT,true)), 3);
            playLitFX(context.getWorld(), pos);
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }
    **/






}
