// FCMOD :)

package net.ivangeevo.self_sustainable.item.items;


import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class WickerWeavingItem extends ProgressiveCraftingItem
{
    // takes half as long as other progressive crafting
    static public final int WICKER_WEAVING_MAX_DAMAGE = (60 * 20 / PROGRESS_TIME_INTERVAL);

    public WickerWeavingItem(Settings settings )
    {
        super(settings);

        /**
        setBuoyant();
        setBellowsBlowDistance(2);
        setIncineratedInCrucible();
        setfurnaceburntime(FurnaceBurnTime.WICKER_PIECE);
        setFilterableProperties(Item.FILTERABLE_THIN);

        setUnlocalizedName( "fcItemWickerWeaving" );
         **/
    }

    @Override
    protected void playCraftingFX(ItemStack stack, World world, LivingEntity player)
    {
        player.playSound(SoundEvents.BLOCK_GRASS_STEP,
                0.25F + 0.25F * (float)world.random.nextInt( 2 ),
                ( world.random.nextFloat() - world.random.nextFloat() ) * 0.25F + 1.75F );
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
    {
        world.playSoundFromEntity(null, user, SoundEvents.BLOCK_GRASS_STEP,
                SoundCategory.PLAYERS , 1.0F, world.random.nextFloat() * 0.1F + 0.9F );

        return new ItemStack( ModItems.WICKER );
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player)
    {

        if (player.timesCraftedThisTick == 0 && world.isClient)
        {
            player.playSound( SoundEvents.BLOCK_GRASS_STEP, 1.0F, world.random.nextFloat() * 0.1F + 0.9F );
        }

        super.onCraft( stack, world, player );
    }


    /**
    @Override
    public boolean getCanBeFedDirectlyIntoCampfire(int iItemDamage)
    {
        return false;
    }

    @Override
    public boolean getCanBeFedDirectlyIntoBrickOven(int iItemDamage)
    {
        return false;
    }
     **/


    @Override
    protected int getProgressiveCraftingMaxDamage()
    {
        return WICKER_WEAVING_MAX_DAMAGE;
    }

    //------------- Class Specific Methods ------------//

    //------------ Client Side Functionality ----------//
}
