package io.github.gtbauke.unnamedtechmod.block;

import io.github.gtbauke.unnamedtechmod.block.base.AbstractMachineBlock;
import io.github.gtbauke.unnamedtechmod.block.entity.BasicPressEntity;
import io.github.gtbauke.unnamedtechmod.block.properties.HeaterType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class BasicHeaterBlock extends AbstractMachineBlock {
    public static final EnumProperty<HeaterType> TYPE = EnumProperty.create("type", HeaterType.class);

    public BasicHeaterBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(TYPE, HeaterType.UNCONNECTED));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockState blockState = pLevel.getBlockState(pPos);
        if (blockState.getValue(TYPE) == HeaterType.UNCONNECTED) {
            return InteractionResult.PASS;
        }

        openContainer(pLevel, pPos, pPlayer);
        return InteractionResult.CONSUME;
    }

    @Override
    protected void openContainer(Level pLevel, BlockPos pPos, Player pPlayer) {
        BlockState blockState = pLevel.getBlockState(pPos);
        if (blockState.getValue(TYPE) == HeaterType.CONNECTED_TO_PRESS) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos.above());

            if (blockEntity instanceof BasicPressEntity entity) {
                NetworkHooks.openScreen((ServerPlayer) pPlayer, entity, pPos.above());
            } else {
                throw new IllegalStateException("Our container provider is missing");
            }
        }
    }

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

        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(TYPE);
    }
}
