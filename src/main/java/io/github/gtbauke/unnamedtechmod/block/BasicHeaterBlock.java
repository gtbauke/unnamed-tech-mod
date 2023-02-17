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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class BasicHeaterBlock extends AbstractMachineBlock {
    public static final EnumProperty<HeaterType> TYPE = EnumProperty.create("type", HeaterType.class);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private static final VoxelShape UNCONNECTED_SHAPE = Stream.of(
            Block.box(14, 10, 2, 16, 12, 14),
            Block.box(0, 0, 0, 16, 10, 16),
            Block.box(0, 10, 0, 16, 12, 2),
            Block.box(0, 10, 14, 16, 12, 16),
            Block.box(0, 10, 2, 2, 12, 14),
            Block.box(0, 10, 2, 2, 12, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape CONNECTED_SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(TYPE) == HeaterType.UNCONNECTED ? UNCONNECTED_SHAPE : CONNECTED_SHAPE;
    }

    public BasicHeaterBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false)
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
        pBuilder.add(LIT);
    }
}
