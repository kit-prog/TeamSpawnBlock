package ru.teamspawn;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TeamSpawnMod.MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TeamSpawnMod.MODID);

    public static final RegistryObject<Block> TEAM_SPAWN_BLOCK =
            BLOCKS.register("team_spawn_block", () -> new TeamSpawnBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .strength(0.5F)
                            .sound(SoundType.AMETHYST)
            ));

    public static final RegistryObject<Item> TEAM_SPAWN_BLOCK_ITEM =
            ITEMS.register("team_spawn_block", () ->
                    new BlockItem(TEAM_SPAWN_BLOCK.get(), new Item.Properties()));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}
