package net.lanzr.itemkit;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ItemKit.MODID)
public class ItemKit {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "itemkit";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public ItemKit(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading

        NeoForge.EVENT_BUS.register(this);

//         Register the entity scan manager for periodic chunk entity cleanup
        NeoForge.EVENT_BUS.register(EntityScanManager.class);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC, "itemkit.toml");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("tyj-trashcan")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    player.openMenu(new SimpleMenuProvider(
                            (id, inv, p) -> new TrashCanMenu(id, inv, new SimpleContainer(54)),
                            Component.literal("垃圾箱，关闭即删除")
                    ));
                    return 1;
                })
        );
        event.getDispatcher().register(Commands.literal("tyj-cleannow")
                .executes(ctx -> {
                    EntityScanManager.cleanNow(ctx.getSource().getServer());
                    return 1;
                })
        );
    }
}
