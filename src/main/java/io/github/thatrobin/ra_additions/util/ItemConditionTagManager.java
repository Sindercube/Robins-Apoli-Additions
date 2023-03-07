package io.github.thatrobin.ra_additions.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

public class ItemConditionTagManager implements ResourceReloader, IdentifiableResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public final TagGroupLoader<ConditionType> tagLoader = new TagGroupLoader<>(this::get, "tags/conditions/item");
    public volatile Map<Identifier, Collection<ConditionType>> tags = Map.of();

    public static ItemConditionTagManager CONDITION_TAG_LOADER = new ItemConditionTagManager();

    public Optional<ConditionType> get(Identifier id) {
        return Optional.of(ItemConditionRegistry.get(id));
    }

    public Collection<ConditionType> getTag(Identifier id) {
        return this.tags.get(id);
    }

    public Collection<ConditionType> getTagOrEmpty(Identifier id) {
        return this.tags.getOrDefault(id, List.of());
    }

    public Stream<Identifier> getTags() {
        return this.tags.keySet().stream();
    }

    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        CompletableFuture<Map<Identifier, List<TagGroupLoader.TrackedEntry>>> completableFuture = CompletableFuture.supplyAsync(() -> this.tagLoader.loadTags(manager), prepareExecutor);
        CompletableFuture<Map<Identifier, CompletableFuture<ConditionType>>> completableFuture2 = CompletableFuture.supplyAsync(() -> manager.findResources("conditions", (id) -> id.getPath().endsWith(".json")), prepareExecutor).thenCompose((ids) -> {
            Map<Identifier, CompletableFuture<ConditionType>> map = Maps.newHashMap();

            CompletableFuture<?>[] completableFutures = (CompletableFuture<?>[])map.values().toArray(new CompletableFuture[0]);
            return CompletableFuture.allOf(completableFutures).handle((unused, ex) -> map);
        });
        CompletableFuture<Pair<Map<Identifier, List<TagGroupLoader.TrackedEntry>>, Map<Identifier, CompletableFuture<ConditionType>>>> var10000 = completableFuture.thenCombine(completableFuture2, Pair::of);
        Objects.requireNonNull(synchronizer);
        return var10000.thenCompose(synchronizer::whenPrepared).thenAcceptAsync((intermediate) -> {
            Map<Identifier, CompletableFuture<ConditionType>> map = intermediate.getSecond();
            ImmutableMap.Builder<Identifier, ConditionType> builder = ImmutableMap.builder();
            map.forEach((id, conditionTypeCompletableFuture) -> conditionTypeCompletableFuture.handle((function, ex) -> {
                if (ex != null) {
                    LOGGER.error("Failed to load item condition {}", id, ex);
                } else {
                    builder.put(id, function);
                }

                return null;
            }).join());
            this.tags = this.tagLoader.buildGroup(intermediate.getFirst());
        }, applyExecutor);
    }

    @Override
    public Identifier getFabricId() {
        return RA_Additions.identifier("item_condition_tags");
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return ImmutableList.of(RA_Additions.identifier("conditions"));
    }
}
