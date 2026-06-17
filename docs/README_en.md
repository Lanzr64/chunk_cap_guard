# Chunk Cap Guard
English | [中文](../README.md)
A **Minecraft** entity cleanup and performance management mod.

Chunk Cap Guard periodically monitors the number of tracked entities in **loaded chunks** across all biomes. When the number of specified entities in a chunk exceeds the configured limit, the mod removes all matching entities from that chunk to prevent localized entity buildup, reduce server lag, and lower the performance impact caused by abnormal mob concentration.

## Features

- Long-interval periodic scanning of target entities in loaded chunks
- Counts specified entities on a per-chunk basis
- Removes all matching entities from a chunk when the target entity count exceeds the limit
- Helps control performance issues caused by localized entity overcrowding
- Supports configurable scan interval, entity limit, and target entity list
- Will broadcast over-limit chunks to all players
- Announcement text that can be changed in the configuration

## How It Works

Chunk Cap Guard performs checks at a fixed interval and processes all loaded chunks in the current world:

1. Iterate through all entities
2. Count target entities by block area
3. If the number of target entities in a block area exceeds the upper limit, clear all matching target entities within that block area

> Note: When a chunk exceeds the configured limit, Chunk Cap Guard removes all configured target entities in that chunk, rather than only the excess amount.

## Example Scenario

Assume the configuration monitors the following entities:

- `minecraft:zombie`
- `minecraft:skeleton`

A chunk contains:

- 30 Zombies
- 25 Skeletons

The total number of tracked target entities in that chunk is `55`.

If the configured per-chunk entity limit is `50`, Chunk Cap Guard will remove all Zombies and Skeletons from that chunk.

## Command
- `tyj cleannow` Execute a cleanup immediately and reset the cleanup timer