package ru.teamspawn;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TeamSpawnMod.MODID)
public class SpawnEvents {
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        var team = player.getTeam();
        if (team == null) return;

        TeamSpawnData.SpawnPoint spawn =
                TeamSpawnData.get(player).getTeamSpawn(team.getName());
        if (spawn == null) return;

        ServerLevel targetLevel = player.getServer().getLevel(spawn.dimension());
        if (targetLevel == null) return;

        BlockPos spawnPos = spawn.pos();
        BlockPos beaconPos = spawnPos.below();

        // The team can use this shared spawn only while its assigned beacon
        // is physically present at the saved location.
        if (!targetLevel.getBlockState(beaconPos).is( ModBlocks.TEAM_SPAWN_BLOCK.get())) {
            player.sendSystemMessage(Component.literal(
                    "§eКомандный маяк уничтожен. Ты возродился на обычном спавне мира."));
            return; // Keep vanilla respawn location (world spawn).
        }

        // Bed-like obstruction check: two clear blocks above the beacon.
        if (!targetLevel.getBlockState(spawnPos).isAir()
                || !targetLevel.getBlockState(spawnPos.above()).isAir()) {
            player.sendSystemMessage(Component.literal(
                    "§cКомандная точка возрождения заблокирована! Ты возродился на обычном спавне мира."));
            return;
        }

        player.teleportTo(targetLevel, spawnPos.getX() + 0.5, spawnPos.getY(),
                spawnPos.getZ() + 0.5, player.getYRot(), player.getXRot());
    }
}
