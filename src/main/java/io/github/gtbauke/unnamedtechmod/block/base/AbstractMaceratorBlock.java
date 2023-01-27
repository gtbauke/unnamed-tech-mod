package io.github.gtbauke.unnamedtechmod.block.base;

import io.github.gtbauke.unnamedtechmod.block.entity.base.MaceratorTileBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMaceratorBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WORKING = BlockStateProperties.LIT;

    protected AbstractMaceratorBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WORKING, Boolean.FALSE));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        if (pStack.hasCustomHoverName()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);

            if (blockEntity instanceof MaceratorTileBase macerator) {
                macerator.setCustomName(pStack.getHoverName());
            }
        }
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, WORKING);
    }

    @Override
    public InteractionResult use(BlockState pState, net.minecraft.world.level.Level pLevel, BlockPos pPos, net.minecraft.world.entity.player.Player pPlayer, net.minecraft.world.InteractionHand pHand, net.minecraft.world.phys.BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        openContainer(pLevel, pPos, pPlayer);
        return InteractionResult.CONSUME;
    }

    protected abstract void openContainer(Level pLevel, BlockPos pPos, Player pPlayer);

    @javax.annotation.Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createMaceratorTicker(Level pLevel, BlockEntityType<T> pServerType, BlockEntityType<? extends MaceratorTileBase> pClientType) {
        return pLevel.isClientSide ? null : createTickerHelper(pServerType, pClientType, MaceratorTileBase::tick);
    }
}
