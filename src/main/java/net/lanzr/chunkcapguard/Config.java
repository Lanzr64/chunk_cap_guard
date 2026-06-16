package net.lanzr.chunkcapguard;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ChunkCapGuard.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ForgeConfigSpec SPEC;
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        setup(builder);
        SPEC = builder.build();
    }

    // Scan interval in ticks (20 ticks = 1 second)
    private static final String COMMON_TAB = "CCG_Common";

    public static ForgeConfigSpec.IntValue SCAN_INTERVAL;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> EXTRA_ENTITY_IDS;
    public static ForgeConfigSpec.IntValue MAX_COUNT_PER_CHUNK;


    public static int scanInterval;
    public static List<? extends String> extraEntityIDS;
    public static int maxCountPerChunk;

    private static boolean validateEntityId(final Object obj) {
        return obj instanceof String id && ResourceLocation.tryParse(id) != null;
    }

    private static void setup(ForgeConfigSpec.Builder builder) {
        SCAN_INTERVAL = builder
                .comment("Scan interval in minutes")
                .defineInRange(COMMON_TAB + ".scanInterval", 30, 1, Integer.MAX_VALUE);

        EXTRA_ENTITY_IDS = builder
                .comment("Additional entity type IDs to monitor (ItemEntity is always tracked by default), e.g. [\"minecraft:item\", \"minecraft:zombie\"]")
                .defineListAllowEmpty(COMMON_TAB + ".extraEntityIds", List.of("minecraft:item","minecraft:zombie"),Config::validateEntityId);

        MAX_COUNT_PER_CHUNK = builder
                .comment("Maximum number of tracked entities per chunk before cleanup")
                .defineInRange(COMMON_TAB + ".maxCountPerChunk", 1000, 1, Integer.MAX_VALUE);
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        scanInterval = SCAN_INTERVAL.get();
        maxCountPerChunk = MAX_COUNT_PER_CHUNK.get();
        extraEntityIDS = EXTRA_ENTITY_IDS.get();
    }
}
