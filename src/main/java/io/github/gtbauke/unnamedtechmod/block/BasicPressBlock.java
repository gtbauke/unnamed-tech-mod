package io.github.gtbauke.unnamedtechmod.block;

import io.github.gtbauke.unnamedtechmod.block.base.AbstractMachineBlock;
import io.github.gtbauke.unnamedtechmod.block.entity.BasicPressEntity;
import io.github.gtbauke.unnamedtechmod.block.entity.ModBlockEntities;
import io.github.gtbauke.unnamedtechmod.block.entity.base.PressTileBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class BasicPressBlock extends AbstractMachineBlock {
    public static final BooleanProperty CONNECTED_TO_HEATER = BooleanProperty.create("connected_to_heater");
    public static final BooleanProperty WORKING = BooleanProperty.create("working");

    public BasicPressBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WORKING, false)
                .setValue(CONNECTED_TO_HEATER, false));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide || pHand != InteractionHand.MAIN_HAND) {
            return InteractionResult.SUCCESS;
        }

        if (!pState.getValue(CONNECTED_TO_HEATER)) {
            pPlayer.displayClientMessage(Component.literal("This press is not connected to a heater! Place a heater below it to work."), true);
            return InteractionResult.SUCCESS;
        }

        if (pHit.getDirection() == pState.getValue(FACING)) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);

            if (blockEntity instanceof BasicPressEntity entity) {
                entity.crankUsed();
            } else {
                throw new IllegalStateException("Our container provider is missing");
            }

            return InteractionResult.SUCCESS;
        }

        openContainer(pLevel, pPos, pPlayer);
        return InteractionResult.CONSUME;
    }

    @Override
    protected void openContainer(Level pLevel, BlockPos pPos, Player pPlayer) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);

        if (blockEntity instanceof BasicPressEntity entity) {
            NetworkHooks.openScreen((ServerPlayer) pPlayer, entity, pPos);
        } else {
            throw new IllegalStateException("Our container provider is missing");
        }
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof PressTileBase press) {
                if (pLevel instanceof ServerLevel) {
                    press.dropContents();
                    //press.getRecipesToAwardAndPopExperience((ServerLevel)pLevel, Vec3.atCenterOf(pPos));
                }

                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }


    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if (pLevel.isClientSide()) return pState;
        if (pDirection != Direction.DOWN) return pState;

        boolean connected = pNeighborState.getBlock() instanceof BasicHeaterBlock;

        if (!connected) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pCurrentPos);
            if (blockEntity instanceof PressTileBase press) {
                if (pLevel instanceof ServerLevel) {
                    press.dropContents();
                    //press.getRecipesToAwardAndPopExperience((ServerLevel)pLevel, Vec3.atCenterOf(pPos));
                }
            }
        }

        return pState.setValue(CONNECTED_TO_HEATER, connected);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        boolean connected = pContext.getLevel().getBlockState(pContext.getClickedPos().below()).getBlock() instanceof BasicHeaterBlock;
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite()).setValue(CONNECTED_TO_HEATER, connected).setValue(WORKING, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(CONNECTED_TO_HEATER);
        pBuilder.add(WORKING);
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ModBlockEntities.BASIC_PRESS.get(), PressTileBase::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (pState.getValue(CONNECTED_TO_HEATER)) {
            return new BasicPressEntity(pPos, pState);
        }

        return null;
    }
}
