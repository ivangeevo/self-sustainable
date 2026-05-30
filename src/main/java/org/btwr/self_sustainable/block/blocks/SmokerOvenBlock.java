package org.btwr.self_sustainable.block.blocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.btwr.self_sustainable.block.interfaces.IgnitableBlock;
import org.btwr.self_sustainable.state.property.ModProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class SmokerOvenBlock extends BlockWithEntity implements IgnitableBlock {

    public static final MapCodec<SmokerOvenBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    createSettingsCodec(),
                    Codec.BOOL.fieldOf("is_mortared").forGetter(SmokerOvenBlock::isMortared)
            ).apply(instance, SmokerOvenBlock::new)
    );
    @Override protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final IntProperty FUEL_LEVEL = ModProperties.FUEL_LEVEL;
    protected final float clickYTopPortion = (6F / 16F);
    protected final float clickYBottomPortion = (6F / 16F);

    private final boolean isMortared;

    public SmokerOvenBlock(Settings settings, boolean isMortared) {
        super(settings);
        this.setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(LIT, false));
        this.isMortared = isMortared;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING, FUEL_LEVEL);
    }

    public boolean isMortared() {
        return isMortared;
    }


    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}