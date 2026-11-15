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
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
            net.minecraft.world.entity.player.Player player, InteractionHand hand,
            net.minecraft.world.phys.BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

        BlockPos spawnPos = pos.above();
        var team = serverPlayer.getTeam();

        if (team != null) {
            TeamSpawnData.get(serverPlayer).setTeamSpawn(
                    team.getName(), level.dimension(), spawnPos);
            serverPlayer.sendSystemMessage(Component.literal(
                    "§aТочка спавна установлена для команды: " + team.getName()));
        } else {
            serverPlayer.setRespawnPosition(
                    level.dimension(), spawnPos, serverPlayer.getYRot(), true, true);
            serverPlayer.sendSystemMessage(Component.literal(
                    "§aТвоя личная точка спавна установлена!"));
        }
        return InteractionResult.CONSUME;
    }
}
