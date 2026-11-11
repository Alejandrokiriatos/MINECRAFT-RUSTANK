package com.rustank.world;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Exports a lightweight, deterministic top-down preview of the planned island.
 * This is a design/test artifact, not a Minecraft world or chunk generator.
 */
public final class WorldPreviewGenerator {
    private WorldPreviewGenerator() { }

    public static BufferedImage render(long seed, int width, int height) {
        if (width < 32 || height < 32 || width > 4096 || height > 4096) {
            throw new IllegalArgumentException("Preview dimensions must be between 32 and 4096 pixels");
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        TerrainProfile terrain = TerrainProfile.forSeed(seed);
        for (int py = 0; py < height; py++) {
            int z = WorldBounds.MIN_Z + (int) ((long) py * WorldBounds.SIZE / height);
            for (int px = 0; px < width; px++) {
                int x = WorldBounds.MIN_X + (int) ((long) px * WorldBounds.SIZE / width);
                TerrainProfile.TerrainSample sample = terrain.sample(x, z);
                image.setRGB(px, py, colorFor(sample).getRGB());
            }
        }

        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setColor(new Color(245, 245, 235));
            graphics.drawRect(0, 0, width - 1, height - 1);
            for (WorldFeaturePlanner.PlannedFeature feature : WorldFeaturePlanner.plan(seed)) {
                int px = toPixelX(feature.x(), width);
                int py = toPixelZ(feature.z(), height);
                int radius = Math.max(4, (int) Math.round(
                        feature.exclusionRadius() * (double) width / WorldBounds.SIZE));
                graphics.setColor(featureColor(feature.type()));
                graphics.fillOval(px - 4, py - 4, 9, 9);
                graphics.drawOval(px - radius, py - radius, radius * 2, radius * 2);
            }
        } finally {
            graphics.dispose();
        }
        return image;
    }

    public static void write(Path output, long seed, int width, int height) throws IOException {
        Path parent = output.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        if (!ImageIO.write(render(seed, width, height), "png", output.toFile())) {
            throw new IOException("No PNG writer is available");
        }
    }

    private static Color colorFor(TerrainProfile.TerrainSample sample) {
        return switch (sample.biome()) {
            case OCEAN -> new Color(24, 76, 112);
            case BEACH -> new Color(220, 202, 145);
            case PLAINS -> shade(new Color(116, 158, 83), sample.surfaceHeight());
            case FOREST -> shade(new Color(48, 105, 61), sample.surfaceHeight());
            case DESERT -> shade(new Color(194, 157, 88), sample.surfaceHeight());
            case ROCKY_HIGHLANDS -> shade(new Color(117, 111, 105), sample.surfaceHeight());
            case OUT_OF_BOUNDS -> new Color(12, 25, 38);
        };
    }

    private static Color shade(Color base, int height) {
        double factor = 0.78 + Math.max(0, height - TerrainProfile.MIN_LAND_HEIGHT) / 220.0;
        return new Color(
                (int) Math.min(255, base.getRed() * factor),
                (int) Math.min(255, base.getGreen() * factor),
                (int) Math.min(255, base.getBlue() * factor));
    }

    private static Color featureColor(WorldFeaturePlanner.FeatureType type) {
        return switch (type) {
            case PLAYER_SPAWN -> new Color(255, 255, 255);
            case AIRFIELD -> new Color(230, 65, 65);
            case HARBOR -> new Color(80, 220, 235);
            case MILITARY_BASE -> new Color(30, 30, 30);
            case RADIATION_ZONE -> new Color(180, 255, 35);
        };
    }

    private static int toPixelX(int blockX, int width) {
        return (int) ((long) (blockX - WorldBounds.MIN_X) * width / WorldBounds.SIZE);
    }

    private static int toPixelZ(int blockZ, int height) {
        return (int) ((long) (blockZ - WorldBounds.MIN_Z) * height / WorldBounds.SIZE);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1 || args.length > 2) {
            throw new IllegalArgumentException("Usage: WorldPreviewGenerator <output.png> [seed]");
        }
        Path output = Path.of(args[0]);
        long seed = args.length == 2 ? Long.parseLong(args[1]) : 20261111L;
        write(output, seed, 1000, 1000);
        System.out.println("Rustank world preview written to " + output.toAbsolutePath()
                + " (seed=" + seed + ", size=4000x4000 blocks)");
    }
}
