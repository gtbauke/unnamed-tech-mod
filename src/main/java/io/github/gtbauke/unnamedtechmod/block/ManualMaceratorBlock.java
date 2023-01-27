package io.github.gtbauke.unnamedtechmod.block;

import io.github.gtbauke.unnamedtechmod.block.base.AbstractMaceratorBlock;
import io.github.gtbauke.unnamedtechmod.block.entity.BasicAlloySmelterEntity;
import io.github.gtbauke.unnamedtechmod.block.entity.ManualMaceratorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

public class ManualMaceratorBlock extends AbstractMaceratorBlock {
    public ManualMaceratorBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, net.minecraft.world.level.Level pLevel, BlockPos pPos, net.minecraft.world.entity.player.Player pPlayer, net.minecraft.world.InteractionHand pHand, net.minecraft.world.phys.BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        Direction direction = pHit.getDirection();
        if (direction == Direction.UP) {
            return InteractionResult.SUCCESS;
        }

        openContainer(pLevel, pPos, pPlayer);
        return InteractionResult.CONSUME;
    }

    @Override
    protected void openContainer(Level pLevel, BlockPos pPos, Player pPlayer) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);

        if (blockEntity instanceof ManualMaceratorEntity entity) {
            NetworkHooks.openScreen((ServerPlayer) pPlayer, entity, pPos);
        } else {
            throw new IllegalStateException("Our container provider is missing");
        }
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ManualMaceratorEntity(pPos, pState);
    }

}
