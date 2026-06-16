package net.lanzr.chunkcapguard;

import net.lanzr.chunkcapguard.api.EntityScanManager;

import net.minecraft.commands.Commands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ChunkCapGuard.MODID)
public class ChunkCapGuard {
    public static final String MODID = "chunkcapguard";

    public ChunkCapGuard(FMLJavaModLoadingContext context) {

        IEventBus modEventBus = context.getModEventBus();
        modEventBus.register(this);

        // Register Forge events on the Forge event bus (RegisterCommandsEvent is not an IModBusEvent)
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);

        context.registerConfig(ModConfig.Type.SERVER, Config.SPEC, "chunkcapguard.toml");
    }

    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("tyj-cleannow")
                .executes(ctx -> {
                    EntityScanManager.cleanNow(ctx.getSource().getServer());
                    return 1;
                }).requires(ctx -> ctx.hasPermission(4))
        );
    }
}
