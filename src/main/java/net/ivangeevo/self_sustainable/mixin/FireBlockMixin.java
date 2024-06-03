package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.block.interfaces.FireBlockAdded;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin extends AbstractFireBlock implements FireBlockAdded
{
    @Shadow @Final public static IntProperty AGE;


    public FireBlockMixin(Settings settings, float damage) {
        super(settings, damage);
    }

    @Inject(method = "scheduledTick", at = @At("HEAD"))
    private void injectedScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci)
    {
        /**
        if ( canFireReplaceBlock(world, pos))
        {
            Block fireBlock = Blocks.FIRE.getDefaultState().getBlock();
            world.setBlockState( pos, fireBlock.getDefaultState() );
        }
        else
        {
            Block block = world.getBlockState(pos).getBlock();

            if ( block != null && block.getCanBeSetOnFireDirectly(world, pos)  )
            {
                block.setOnFireDirectly(world, pos);
            }
        }
         **/
    }
    @Override
    public void checkForFireSpreadFromLocation(World world, BlockPos pos, Random random, int iSourceFireAge)
    {
        if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK))
        {
            //boolean bHighHumidity = world.getBiomeAccess().

            for ( int iTempI = pos.getX() - 1; iTempI <= pos.getX() + 1; ++iTempI )
            {
                for ( int iTempK = pos.getZ() - 1; iTempK <= pos.getZ() + 1; ++iTempK )
                {
                    for ( int iTempJ = pos.getY() - 1; iTempJ <= pos.getY() + 4; ++iTempJ )
                    {
                        if ( iTempI != pos.getX() || iTempJ != pos.getY() || iTempK != pos.getZ() )
                        {
                            int iSpreadTopBound = 100;

                            if ( iTempJ > pos.getY() + 1 )
                            {
                                iSpreadTopBound += ( iTempJ - ( pos.getY() + 1 ) ) * 100;
                            }

                            checkForFireSpreadToOneBlockLocation(world, pos, random, iSourceFireAge, false, iSpreadTopBound);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void checkForFireSpreadToOneBlockLocation(World world, BlockPos pos, Random rand, int iSourceFireAge,boolean bHighHumidity, int iSpreadTopBound) {
        if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK))
        {
            int iNeighborChance = getChanceOfNeighborsEncouragingFireCustom(world, pos);

            if ( iNeighborChance > 0 )
            {
                int iSpreadChance = (iNeighborChance + 61 ) / ( iSourceFireAge + 30 );

                //TODO: Add high humidity halved spread chance.
                /**
                 if (bHighHumidity)
                 {
                 iSpreadChance /= 2;
                 }
                 **/

                if ( iSpreadChance > 0 && rand.nextInt( iSpreadTopBound ) <= iSpreadChance &&
                        ( !world.isRaining() || !world.hasRain(pos)) &&
                        !world.hasRain(pos.south()) && !world.hasRain(pos.north()) &&
                        !world.hasRain(pos.west()) && !world.hasRain(pos.east()))
                {
                    int iStartMetadata = iSourceFireAge + rand.nextInt( 5 ) / 4;

                    if (iStartMetadata > 15)
                    {
                        iStartMetadata = 15;
                    }

                    if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK))
                    {
                        if ( canFireReplaceBlock(world, pos) )
                        {
                            world.setBlockState( pos, Blocks.FIRE.getDefaultState().with(AGE, iStartMetadata) );
                        }
                        else
                        {
                            Block block = world.getBlockState(pos).getBlock();

                            if ( block != null && block.getCanBeSetOnFireDirectly(world, pos)  )
                            {
                                block.setOnFireDirectly(world, pos);
                            }
                        }
                    }
                }
            }
        }

    }

    @Unique
    private static int getChanceOfNeighborsEncouragingFireCustom(World world, BlockPos pos)
    {
        // copied from BlockFire due to it being non-static

        if ( !canFireReplaceBlock(world, pos) )
        {
            Block block = world.getBlockState(pos).getBlock();

            if ( block != null && block.getCanBeSetOnFireDirectly(world, pos)  )
            {
                return block.getChanceOfFireSpreadingDirectlyTo(world, pos);
            }
            else
            {
                return 0;
            }
        }
        else
        {
            int iChance = getChanceToEncourageFire(world, pos.north(), 0);

            iChance = getChanceToEncourageFire(world, pos.south(), iChance);
            iChance = getChanceToEncourageFire(world, pos.down(), iChance);
            iChance = getChanceToEncourageFire(world, pos.up(), iChance);
            iChance = getChanceToEncourageFire(world, pos.west(), iChance);
            iChance = getChanceToEncourageFire(world, pos.east(), iChance);

            return iChance;
        }
    }

    private static int getChanceToEncourageFire(World world, BlockPos pos, int iPrevChance)
    {
        // just copied from BlockFire due to it being non-static
        FireBlock fireBlock = (FireBlock) world.getBlockState(pos).getBlock();
        int iChance = fireBlock.getSpreadChance(world.getBlockState(pos));

        return Math.max(iChance, iPrevChance);
    }

    private static boolean canFireReplaceBlock(World world, BlockPos pos)
    {
        Block block = world.getBlockState(pos).getBlock();

        return block == null || world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK) /** && block.getCanBlockBeReplacedByFire(world, i, j, k) **/;
    }

}
