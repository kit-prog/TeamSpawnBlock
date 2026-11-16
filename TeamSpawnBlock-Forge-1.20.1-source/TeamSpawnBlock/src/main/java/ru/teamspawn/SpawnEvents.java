package ru.teamspawn;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TeamSpawnMod.MODID)
public class SpawnEvents {
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        var team = player.getTeam();
        // Personal respawn behavior is unchanged for players without a team.
        if (team == null) return;

        TeamSpawnData.SpawnPoint spawn =
                TeamSpawnData.get(player).getTeamSpawn(team.getName());

        // No saved team beacon means the team's beacon was removed (or never set).
        // Explicitly use the world's original shared spawn, not vanilla's possibly
        // stale personal bed/beacon respawn position.
        if (spawn == null) {
            teleportToWorldSpawn(player);
            player.sendSystemMessage(Component.literal(
                    "§eКомандный маяк не найден. Ты возродился на начальном спавне мира."));
            return;
        }

        ServerLevel targetLevel = player.getServer().getLevel(spawn.dimension());
        if (targetLevel == null) {
            teleportToWorldSpawn(player);
            return;
        }

        BlockPos spawnPos = spawn.pos();
        BlockPos beaconPos = spawnPos.below();

        // The team can use this shared spawn only while its assigned beacon
        // is physically present at the saved location.
        if (!targetLevel.getBlockState(beaconPos).is(ModBlocks.TEAM_SPAWN_BLOCK.get())) {
            teleportToWorldSpawn(player);
            player.sendSystemMessage(Component.literal(
                    "§eКомандный маяк уничтожен. Ты возродился на начальном спавне мира."));
            return;
        }

        // If the space above the beacon is blocked, fall back to the world spawn.
        if (!targetLevel.getBlockState(spawnPos).isAir()
                || !targetLevel.getBlockState(spawnPos.above()).isAir()) {
            teleportToWorldSpawn(player);
            player.sendSystemMessage(Component.literal(
                    "§cМесто у командного маяка заблокировано. Ты возродился на начальном спавне мира."));
            return;
        }

        player.teleportTo(targetLevel, spawnPos.getX() + 0.5, spawnPos.getY(),
                spawnPos.getZ() + 0.5, player.getYRot(), player.getXRot());
    }

    private static void teleportToWorldSpawn(ServerPlayer player) {
        ServerLevel overworld = player.getServer().overworld();
        BlockPos worldSpawn = overworld.getSharedSpawnPos();

        player.teleportTo(overworld, worldSpawn.getX() + 0.5, worldSpawn.getY(),
                worldSpawn.getZ() + 0.5, overworld.getSharedSpawnAngle(), player.getXRot());
    }
}
