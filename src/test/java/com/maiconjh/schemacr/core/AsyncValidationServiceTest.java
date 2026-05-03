package com.maiconjh.schemacr.core;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaRegistry;
import com.maiconjh.schemacr.schemes.SchemaType;
import com.maiconjh.schemacr.validation.ValidationResult;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class AsyncValidationServiceTest {
    @Test
    void validateAsyncCompletesSuccessfully() throws Exception {
        SchemaValidatorPlugin plugin = Mockito.mock(SchemaValidatorPlugin.class);
        SchemaRegistry registry = Mockito.mock(SchemaRegistry.class);
        ValidationService service = Mockito.mock(ValidationService.class);
        BukkitScheduler scheduler = Mockito.mock(BukkitScheduler.class);
        PluginManager pluginManager = Mockito.mock(PluginManager.class);

        try (var bukkit = Mockito.mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getScheduler).thenReturn(scheduler);
            bukkit.when(Bukkit::getPluginManager).thenReturn(pluginManager);
            when(scheduler.runTask(eq(plugin), any())).thenAnswer(inv -> {((Runnable)inv.getArgument(1)).run(); return null;});
            Schema schema = Schema.builder("test", SchemaType.OBJECT).build();
            when(plugin.getSchemaRegistry()).thenReturn(registry);
            when(plugin.getValidationService()).thenReturn(service);
            when(registry.getSchema("test")).thenReturn(java.util.Optional.of(schema));
            when(service.validate(any(), eq(schema))).thenReturn(ValidationResult.success());
            AsyncValidationService async = new AsyncValidationService(plugin, 2, 100);
            assertTrue(async.validateAsync(java.util.Map.of(), "test").get().isSuccess());
            async.shutdown();
        }
    }

    @Test
    void validatesTwentyInParallel() {
        SchemaValidatorPlugin plugin = Mockito.mock(SchemaValidatorPlugin.class);
        SchemaRegistry registry = Mockito.mock(SchemaRegistry.class);
        ValidationService service = Mockito.mock(ValidationService.class);
        BukkitScheduler scheduler = Mockito.mock(BukkitScheduler.class);
        PluginManager pluginManager = Mockito.mock(PluginManager.class);
        try (var bukkit = Mockito.mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getScheduler).thenReturn(scheduler);
            bukkit.when(Bukkit::getPluginManager).thenReturn(pluginManager);
            when(scheduler.runTask(eq(plugin), any())).thenAnswer(inv -> {((Runnable)inv.getArgument(1)).run(); return null;});
            Schema schema = Schema.builder("test", SchemaType.OBJECT).build();
            when(plugin.getSchemaRegistry()).thenReturn(registry);
            when(plugin.getValidationService()).thenReturn(service);
            when(registry.getSchema("test")).thenReturn(java.util.Optional.of(schema));
            when(service.validate(any(), eq(schema))).thenReturn(ValidationResult.success());
            AsyncValidationService async = new AsyncValidationService(plugin, 4, 100);
            List<CompletableFuture<ValidationResult>> futures = new ArrayList<>();
            for (int i = 0; i < 20; i++) futures.add(async.validateAsync(java.util.Map.of("id", i), "test"));
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
            assertTrue(futures.stream().allMatch(CompletableFuture::isDone));
            async.shutdown();
        }
    }
}
