package ru.teamspawn;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.util.datafix.DataFixTypes;

public class TeamSpawnData extends SavedData {
    private static final String DATA_NAME = "teamspawn_data";
    private CompoundTag teamSpawns = new CompoundTag();

    public static TeamSpawnData load(CompoundTag tag) {
        TeamSpawnData data = new TeamSpawnData();
        data.teamSpawns = tag.getCompound("TeamSpawns").copy();
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.put("TeamSpawns", teamSpawns.copy());
        return tag;
    }

    public void setTeamSpawn(String teamName, ResourceKey<Level> dimension, BlockPos pos) {
        CompoundTag entry = new CompoundTag();
        entry.putString("Dimension", dimension.location().toString());
        entry.putInt("X", pos.getX());
        entry.putInt("Y", pos.getY());
        entry.putInt("Z", pos.getZ());
        teamSpawns.put(teamName, entry);
        setDirty();
    }

    public SpawnPoint getTeamSpawn(String teamName) {
        if (!teamSpawns.contains(teamName, 10)) return null;
        CompoundTag entry = teamSpawns.getCompound(teamName);
        ResourceLocation location = ResourceLocation.tryParse(entry.getString("Dimension"));
        if (location == null) return null;

        ResourceKey<Level> dimension =
                ResourceKey.create(Registries.DIMENSION, location);
        BlockPos pos = new BlockPos(entry.getInt("X"), entry.getInt("Y"), entry.getInt("Z"));
        return new SpawnPoint(dimension, pos);
    }

    public static TeamSpawnData get(ServerPlayer player) {
        ServerLevel overworld = player.getServer().overworld();
        DimensionDataStorage storage = overworld.getDataStorage();
        return storage.computeIfAbsent(
                new SavedData.Factory<>(TeamSpawnData::new, TeamSpawnData::load, DataFixTypes.LEVEL),
                DATA_NAME);
    }

    public record SpawnPoint(ResourceKey<Level> dimension, BlockPos pos) {}
}
