package net.lanzr.chunkcapguard;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = ChunkCapGuard.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ModConfigSpec SPEC;
    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        setup(builder);
        SPEC = builder.build();
    }

    // Scan interval in ticks (20 ticks = 1 second)
    private static final String COMMON_TAB = "CCG_Common";

    public static ModConfigSpec.IntValue SCAN_INTERVAL;
    public static ModConfigSpec.ConfigValue<List<? extends String>> EXTRA_ENTITY_IDS;
    public static ModConfigSpec.IntValue MAX_COUNT_PER_CHUNK;


    public static int scanInterval;
    public static List<? extends String> extraEntityIDS;
    public static int maxCountPerChunk;

    private static boolean validateEntityId(final Object obj) {
        return obj instanceof String id && ResourceLocation.tryParse(id) != null;
    }

    private static void setup(ModConfigSpec.Builder builder) {
        SCAN_INTERVAL = builder
                .comment("Scan interval in minutes")
                .defineInRange(COMMON_TAB + ".scanInterval", 30, 1, Integer.MAX_VALUE);

        EXTRA_ENTITY_IDS = builder
                .comment("Additional entity type IDs to monitor (ItemEntity is always tracked by default), e.g. [\"minecraft:item\", \"minecraft:zombie\"]")
                .defineListAllowEmpty(COMMON_TAB + ".extraEntityIds", List.of("minecraft:item","minecraft:zombie"), () -> "", Config::validateEntityId);

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
