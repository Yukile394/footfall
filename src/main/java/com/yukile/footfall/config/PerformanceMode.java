package com.yukile.footfall.config;

/**
 * Coarse performance presets. Each preset scales render distance and the
 * global footprint cap so players can trade visual density for FPS without
 * touching every individual slider.
 */
public enum PerformanceMode {
    LOW(24.0, 400, 2),
    BALANCED(48.0, 1500, 1),
    ULTRA(96.0, 4000, 1);

    private final double renderDistance;
    private final int footprintCap;
    private final int spawnSkip;

    PerformanceMode(double renderDistance, int footprintCap, int spawnSkip) {
        this.renderDistance = renderDistance;
        this.footprintCap = footprintCap;
        this.spawnSkip = spawnSkip;
    }

    public double renderDistance() {
        return renderDistance;
    }

    public int footprintCap() {
        return footprintCap;
    }

    /** Spawn only every Nth eligible step; 1 = every step. */
    public int spawnSkip() {
        return spawnSkip;
    }

    public PerformanceMode next() {
        PerformanceMode[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }
}
