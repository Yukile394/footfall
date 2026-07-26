package com.yukile.footfall.client;

import com.yukile.footfall.config.FootfallConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * Plain vanilla-widget config screen (no external config-library
 * dependency required). Every value is applied live to the shared
 * {@link FootfallConfig} instance and written to disk on close.
 */
public final class FootfallConfigScreen extends Screen {

    private final Screen parent;
    private final FootfallConfig config = FootfallConfig.get();

    protected FootfallConfigScreen(Screen parent) {
        super(Text.translatable("footfall.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 100;
        int y = this.height / 2 - 110;
        int rowHeight = 22;

        addToggle(x, y, "footfall.config.mod_enabled", config.modEnabled, v -> config.modEnabled = v);
        y += rowHeight;
        addToggle(x, y, "footfall.config.footprints_enabled", config.footprintsEnabled, v -> config.footprintsEnabled = v);
        y += rowHeight;
        addToggle(x, y, "footfall.config.mob_footprints", config.mobFootprintsEnabled, v -> config.mobFootprintsEnabled = v);
        y += rowHeight;
        addToggle(x, y, "footfall.config.snow_covers", config.snowCoversFootprints, v -> config.snowCoversFootprints = v);
        y += rowHeight;

        this.addDrawableChild(new LabeledIntSlider(x, y, 200, 20,
                "footfall.config.max_footprints", 50, 5000, config.maxFootprints,
                v -> config.maxFootprints = v));
        y += rowHeight;

        this.addDrawableChild(new LabeledIntSlider(x, y, 200, 20,
                "footfall.config.lifetime", 5, 300, config.lifetimeSeconds,
                v -> config.lifetimeSeconds = v));
        y += rowHeight;

        this.addDrawableChild(new LabeledFloatSlider(x, y, 200, 20,
                "footfall.config.size", 0.3f, 3.0f, config.footprintSize,
                v -> config.footprintSize = v));
        y += rowHeight;

        this.addDrawableChild(new LabeledFloatSlider(x, y, 200, 20,
                "footfall.config.density", 0.25f, 4.0f, config.footprintDensity,
                v -> config.footprintDensity = v));
        y += rowHeight;

        this.addDrawableChild(new LabeledFloatSlider(x, y, 200, 20,
                "footfall.config.rain_fade", 1.0f, 10.0f, config.rainFadeMultiplier,
                v -> config.rainFadeMultiplier = v));
        y += rowHeight;

        this.addDrawableChild(CyclingButtonWidget.<com.yukile.footfall.config.PerformanceMode>builder(
                        mode -> Text.translatable("footfall.config.performance." + mode.name().toLowerCase()))
                .values(com.yukile.footfall.config.PerformanceMode.values())
                .initially(config.performanceMode)
                .build(x, y, 200, 20,
                        Text.translatable("footfall.config.performance"),
                        (btn, mode) -> config.performanceMode = mode));
        y += rowHeight + 10;

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), btn -> close())
                .dimensions(x, y, 200, 20).build());
    }

    private void addToggle(int x, int y, String key, boolean initial, Consumer<Boolean> setter) {
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(initial)
                .build(x, y, 200, 20, Text.translatable(key), (btn, value) -> setter.accept(value)));
    }

    @Override
    public void close() {
        config.save();
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    /** Minimal integer slider with a live label. */
    private static final class LabeledIntSlider extends SliderWidget {
        private final String key;
        private final int min;
        private final int max;
        private final IntConsumer setter;

        LabeledIntSlider(int x, int y, int width, int height, String key, int min, int max, int initial,
                         IntConsumer setter) {
            super(x, y, width, height, Text.empty(), (initial - min) / (double) (max - min));
            this.key = key;
            this.min = min;
            this.max = max;
            this.setter = setter;
            updateMessage();
        }

        private int currentValue() {
            return (int) Math.round(min + this.value * (max - min));
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.translatable(key, currentValue()));
        }

        @Override
        protected void applyValue() {
            setter.accept(currentValue());
        }
    }

    /** Minimal float slider with a live label. */
    private static final class LabeledFloatSlider extends SliderWidget {
        private final String key;
        private final float min;
        private final float max;
        private final Consumer<Float> setter;

        LabeledFloatSlider(int x, int y, int width, int height, String key, float min, float max, float initial,
                           Consumer<Float> setter) {
            super(x, y, width, height, Text.empty(), (initial - min) / (double) (max - min));
            this.key = key;
            this.min = min;
            this.max = max;
            this.setter = setter;
            updateMessage();
        }

        private float currentValue() {
            return (float) (min + this.value * (max - min));
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.translatable(key, String.format("%.2f", currentValue())));
        }

        @Override
        protected void applyValue() {
            setter.accept(currentValue());
        }
    }
}
