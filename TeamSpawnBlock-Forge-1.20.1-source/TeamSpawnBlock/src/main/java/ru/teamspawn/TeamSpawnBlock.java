package ru.teamspawn;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TeamSpawnBlock extends Block {
    public TeamSpawnBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos,
            BlockState newState, boolean isMoving) {
        if (!level.isClientSide && state.getBlock() != newState.getBlock()
                && level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            TeamSpawnData.get(serverLevel).clearTeamSpawnAt(level.dimension(), pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
            net.minecraft.world.entity.player.Player player, InteractionHand hand,
            net.minecraft.world.phys.BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

        // Like a bed: there must be room for a player to stand at the spawn.
        BlockPos spawnPos = pos.above();
        if (!level.getBlockState(spawnPos).isAir()
                || !level.getBlockState(spawnPos.above()).isAir()) {
            serverPlayer.sendSystemMessage(Component.literal(
                    "§cТочка возрождения заблокирована! Освободи два блока над маяком."));
            return InteractionResult.CONSUME;
        }

        var team = serverPlayer.getTeam();
        if (team != null) {
            TeamSpawnData.get(serverPlayer).setTeamSpawn(
                    team.getName(), level.dimension(), spawnPos);
            serverPlayer.sendSystemMessage(Component.literal(
                    "§aТочка возрождения команды установлена! Все участники команды будут появляться здесь."));
        } else {
            serverPlayer.setRespawnPosition(
                    level.dimension(), spawnPos, serverPlayer.getYRot(), true, true);
            serverPlayer.sendSystemMessage(Component.literal(
                    "§aТвоя личная точка возрождения установлена!"));
        }
        return InteractionResult.CONSUME;
    }
}
