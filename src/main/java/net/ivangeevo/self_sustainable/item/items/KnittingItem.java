package net.ivangeevo.self_sustainable.item.items;

import net.ivangeevo.self_sustainable.item.ModItems;
import net.ivangeevo.self_sustainable.util.ItemUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

public class KnittingItem extends ProgressiveCraftingItem
{
    public KnittingItem(Settings settings)
    {
        super(settings);
    }


    @Override
    protected void playCraftingFX(ItemStack stack, World world, LivingEntity player)
    {
        player.playSound(SoundEvents.BLOCK_WOOD_STEP,
                0.25F + 0.25F * (float)world.random.nextInt( 2 ),
                ( world.random.nextFloat() - world.random.nextFloat() ) * 0.25F + 1.75F );
    }


    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        int iColorIndex = WoolItem.getClosestColorIndex(getColor(stack));

        // Create a new ItemStack with the specified item
        ItemStack woolStack = new ItemStack(ModItems.WOOL_KNIT, 1);

        // Set color information in the NBT of the woolStack
        NbtCompound woolNBT = woolStack.getOrCreateNbt();
        woolNBT.putInt("color", iColorIndex);
        woolStack.setNbt(woolNBT);

        PlayerEntity player = (PlayerEntity)user;

        // Play sound and give the new ItemStack to the player
        world.playSound(player, player.getBlockPos(), SoundEvents.BLOCK_WOOL_STEP, SoundCategory.BLOCKS, 1F, world.getRandom().nextFloat() * 0.1F + 0.9F);
        ItemUtils.givePlayerStackOrEject(player, woolStack);

        // Return a new ItemStack of knitting needles
        return new ItemStack(ModItems.KNITTING_NEEDLES);
    }

    @Override
    public boolean getCanBeFedDirectlyIntoCampfire(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean getCanBeFedDirectlyIntoBrickOven(ItemStack stack)
    {
        return false;
    }


    static public void setColor(ItemStack stack, DyeColor iColor)
    {
        NbtCompound tag = stack.getNbt();

        if ( tag == null )
        {
            tag = new NbtCompound();
            stack.setNbt( tag );
        }

        tag.putInt( "fcColor", iColor.getId() );

    }

    static public int getColor(ItemStack stack)
    {
        NbtCompound tag = stack.getNbt();

        if ( tag != null )
        {
            if ( tag.contains( "fcColor" ) )
            {
                return tag.getInt( "fcColor" );
            }
        }

        return 0;
    }
}
