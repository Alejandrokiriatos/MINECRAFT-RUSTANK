package com.rustank.world;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class WorldPreviewGeneratorTest {
    @Test
    void renderProducesRequestedImageDimensions() {
        BufferedImage image = WorldPreviewGenerator.render(20261111L, 96, 80);
        assertEquals(96, image.getWidth());
        assertEquals(80, image.getHeight());
    }

    @Test
    void sameSeedProducesIdenticalPreviewPixels() {
        BufferedImage first = WorldPreviewGenerator.render(1234L, 64, 64);
        BufferedImage second = WorldPreviewGenerator.render(1234L, 64, 64);
        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) {
                assertEquals(first.getRGB(x, y), second.getRGB(x, y));
            }
        }
    }

    @Test
    void rejectsUnreasonableImageDimensions() {
        assertThrows(IllegalArgumentException.class,
                () -> WorldPreviewGenerator.render(1L, 16, 16));
        assertThrows(IllegalArgumentException.class,
                () -> WorldPreviewGenerator.render(1L, 5000, 64));
    }
}
