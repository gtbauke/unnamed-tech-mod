package io.github.gtbauke.unnamedtechmod.block;

import io.github.gtbauke.unnamedtechmod.UnnamedTechMod;
import io.github.gtbauke.unnamedtechmod.block.base.AbstractMachineBlock;
import io.github.gtbauke.unnamedtechmod.block.properties.HeaterType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class BasicHeaterBlock extends AbstractMachineBlock {
    public static final EnumProperty<HeaterType> TYPE = EnumProperty.create("type", HeaterType.class);

    public BasicHeaterBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void openContainer(Level pLevel, BlockPos pPos, Player pPlayer) { }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        HeaterType type = HeaterType.UNCONNECTED;
        if (candidatePartnerIsValid(pContext)){
            type = HeaterType.CONNECTED_TO_PRESS;
        }

        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite()).setValue(TYPE, type);
    }

    private boolean candidatePartnerIsValid(BlockPlaceContext pContext) {
        BlockState blockstate = pContext.getLevel().getBlockState(pContext.getClickedPos().relative(Direction.UP));
        return blockstate.getBlock() instanceof BasicPressBlock;
    }

    public Logger LOGGER = UnnamedTechMod.LOGGER;
    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if (pLevel.isClientSide()) return pState;
        if (pDirection != Direction.UP) return pState;

        if (pNeighborState.getBlock() instanceof AirBlock) {
            return pState.setValue(TYPE, HeaterType.UNCONNECTED);
        }
        else if (pNeighborState.getBlock() instanceof BasicPressBlock){
            return pState.setValue(TYPE, HeaterType.CONNECTED_TO_PRESS);
        }

        LOGGER.debug("pDir " + pDirection.getName() +  " pNeighbourState " + pNeighborState);

        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(TYPE);
    }
}
