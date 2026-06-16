package net.lanzr.chunkcapguard;

import com.example.examplemod.ExampleMod;
import net.lanzr.chunkcapguard.api.EntityScanManager;

import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ExampleMod.MODID)
public class ChunkCapGuard {
    public static final String MODID = "chunkcapguard";

    public ChunkCapGuard(FMLJavaModLoadingContext context) {

        IEventBus modEventBus = context.getModEventBus();
        modEventBus.register(this);

        modEventBus.register(EntityScanManager.class);

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC, "chunkcapguard.toml");
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
