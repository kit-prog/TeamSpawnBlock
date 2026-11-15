package ru.teamspawn;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TeamSpawnMod.MODID)
public class TeamSpawnMod {
    public static final String MODID = "teamspawn";

    public TeamSpawnMod() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.register(bus);
        bus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModBlocks.TEAM_SPAWN_BLOCK_ITEM);
        }
    }
}
