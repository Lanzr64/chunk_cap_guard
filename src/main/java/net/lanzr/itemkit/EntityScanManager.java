package net.lanzr.itemkit;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntityScanManager {
    private static int tickCounter = 0;
    private static final int CHECK_PERIOD = 20 * 60; // 1分钟检查一次

    public static void cleanNow(MinecraftServer server) {
        tickCounter = 0;
        clieanHandler(server);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        var server = event.getServer();
        if (server.getTickCount() % CHECK_PERIOD != 0) {
            return;
        }
        if (server == null) return;

        int intervalCount = Config.scanInterval;

        if (intervalCount <= 0) return;

        tickCounter++;
        if (tickCounter < intervalCount) return;
        tickCounter = 0;

        clieanHandler(server);

    }

    private static void clieanHandler(MinecraftServer server) {
        // Resolve additional entity types from config
        Set<EntityType<?>> extraTypes = new HashSet<>();
        for (String id : Config.extraEntityIDS) {
            ResourceLocation loc = ResourceLocation.tryParse(id);
            if (loc != null) {
                BuiltInRegistries.ENTITY_TYPE.getOptional(loc).ifPresent(extraTypes::add);
            }
        }

        // Scan all dimensions
        for (ServerLevel level : server.getAllLevels()) {
            scanLevel(level, extraTypes, Config.maxCountPerChunk);
        }
    }

    private static void scanLevel(ServerLevel level, Set<EntityType<?>> extraTypes, int maxCount) {
        Map<ChunkPos, List<Entity>> trackedByChunk = new HashMap<>();

        for (Entity entity : level.getEntities().getAll()) {
//            System.out.println(entity.getType());
            // Always track ItemEntity (dropped items)
            if (extraTypes.contains(entity.getType())) {
                trackedByChunk.computeIfAbsent(entity.chunkPosition(), k -> new ArrayList<>()).add(entity);
            }
        }

        String dimId = level.dimension().location().toString();

        for (Map.Entry<ChunkPos, List<Entity>> entry : trackedByChunk.entrySet()) {
            ChunkPos chunkPos = entry.getKey();
            List<Entity> entities = entry.getValue();

            if (entities.size() <= maxCount) continue;

            // Broadcast warning to all online players
            Component message = Component.literal(
                    "§c[土窑炉] §7区块 §f[" + dimId + ": " + chunkPos.x + ", " + chunkPos.z + "]§7 "
                            + "有 §e" + entities.size() + "§7 个限制实体, 顷刻焚化 !"
            );

            for (ServerPlayer player : level.players()) {
                player.sendSystemMessage(message);
            }

//            ItemKit.LOGGER.info(
//                    "Cleaned {} tracked entities in chunk [{}: {}, {}]",
//                    entities.size(), dimId, chunkPos.x, chunkPos.z
//            );

            for (Entity entity : entities) {
                entity.discard();
            }
        }
    }
}
