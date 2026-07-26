package com.yukile.footfall.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Holds every user-facing setting for Footfall and persists them to
 * {@code config/footfall.json}. This is a plain data object (no external
 * config library dependency) so the mod has zero required runtime
 * dependencies beyond Fabric API.
 */
public final class FootfallConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "footfall.json";

    private static FootfallConfig instance;

    // --- General ---
    public boolean modEnabled = true;
    public boolean footprintsEnabled = true;
    public boolean mobFootprintsEnabled = false;

    // --- Lifetime / density ---
    public int maxFootprints = 1500;
    public int lifetimeSeconds = 45;
    public float footprintSize = 1.0f;
    public float footprintDensity = 1.0f; // multiplies steps-per-print (lower = denser)

    // --- Weather ---
    public float rainFadeMultiplier = 3.0f;
    public boolean snowCoversFootprints = true;

    // --- Performance ---
    public PerformanceMode performanceMode = PerformanceMode.BALANCED;

    public static FootfallConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    private static FootfallConfig load() {
        Path path = configPath();
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                FootfallConfig loaded = GSON.fromJson(reader, FootfallConfig.class);
                if (loaded != null) {
                    return loaded;
                }
            } catch (IOException | com.google.gson.JsonSyntaxException e) {
                System.err.println("[Footfall] Config could not be read, using defaults: " + e.getMessage());
            }
        }
        FootfallConfig fresh = new FootfallConfig();
        fresh.save();
        return fresh;
    }

    public void save() {
        Path path = configPath();
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            System.err.println("[Footfall] Config could not be saved: " + e.getMessage());
        }
    }
}
