package net.lanzr.chunkcapguard;

import net.lanzr.chunkcapguard.api.EntityScanManager;

import net.minecraft.commands.Commands;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(ChunkCapGuard.MODID)
public class ChunkCapGuard {
    public static final String MODID = "chunkcapguard";

    public ChunkCapGuard(IEventBus modEventBus, ModContainer modContainer) {

        NeoForge.EVENT_BUS.register(this);

        NeoForge.EVENT_BUS.register(EntityScanManager.class);

        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC, "chunkcapguard.toml");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("tyj-cleannow")
                .executes(ctx -> {
                    EntityScanManager.cleanNow(ctx.getSource().getServer());
                    return 1;
                }).requires(ctx -> ctx.hasPermission(4))
        );
    }
}
