package ru.teamspawn;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

        var pos = spawn.pos();
        player.teleportTo(targetLevel, pos.getX() + 0.5, pos.getY(),
                pos.getZ() + 0.5, player.getYRot(), player.getXRot());
    }
}
