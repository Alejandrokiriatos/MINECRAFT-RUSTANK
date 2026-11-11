package com.rustank.survival;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * Versioned, atomic per-player storage for Rustank survival needs.
 *
 * <p>Forge's PlayerEvent.LoadFromFile and SaveToFile events own the file
 * lifecycle. This class only handles the binary format and has no Forge
 * dependencies, so its compatibility and corruption handling can be tested
 * without launching Minecraft.</p>
 */
public final class SurvivalNeedsPersistence {
    private static final int MAGIC = 0x5253544B; // RSTK
    private static final int FORMAT_VERSION = 1;

    private SurvivalNeedsPersistence() {
    }

    /**
     * Writes to a temporary sibling file, then atomically replaces the target
     * when the filesystem supports atomic moves.
     */
    public static void save(Path target, SurvivalNeedsState.Snapshot snapshot) throws IOException {
        if (target == null) {
            throw new IllegalArgumentException("target cannot be null");
        }
        if (snapshot == null) {
            throw new IllegalArgumentException("snapshot cannot be null");
        }

        Path absoluteTarget = target.toAbsolutePath();
        Path parent = absoluteTarget.getParent();
        if (parent == null) {
            throw new IOException("Player data path has no parent directory");
        }

        Files.createDirectories(parent);
        Path temporary = Files.createTempFile(parent, "rustank-needs-", ".tmp");
        boolean moved = false;
        try {
            try (DataOutputStream output = new DataOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(temporary)))) {
                output.writeInt(MAGIC);
                output.writeInt(FORMAT_VERSION);
                output.writeInt(snapshot.hunger().hunger());
                output.writeInt(snapshot.hunger().drainProgress());
                output.writeInt(snapshot.hydration().hydration());
                output.writeInt(snapshot.hydration().drainProgress());
            }

            try {
                Files.move(temporary, absoluteTarget,
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, absoluteTarget, StandardCopyOption.REPLACE_EXISTING);
            }
            moved = true;
        } finally {
            if (!moved) {
                Files.deleteIfExists(temporary);
            }
        }
    }

    /**
     * Loads a saved snapshot. An absent file means a new player and returns
     * Optional.empty(); a malformed or unsupported file fails with IOException.
     */
    public static Optional<SurvivalNeedsState.Snapshot> load(Path source) throws IOException {
        if (source == null) {
            throw new IllegalArgumentException("source cannot be null");
        }
        if (!Files.exists(source)) {
            return Optional.empty();
        }
        if (!Files.isRegularFile(source)) {
            throw new IOException("Player needs path is not a regular file");
        }

        try (DataInputStream input = new DataInputStream(
                new BufferedInputStream(Files.newInputStream(source)))) {
            int magic = input.readInt();
            if (magic != MAGIC) {
                throw new IOException("Invalid Rustank needs file signature");
            }

            int version = input.readInt();
            if (version != FORMAT_VERSION) {
                throw new IOException("Unsupported Rustank needs file version: " + version);
            }

            int hunger = input.readInt();
            int hungerProgress = input.readInt();
            int hydration = input.readInt();
            int hydrationProgress = input.readInt();

            validateRange("hunger", hunger, HungerState.MIN_HUNGER, HungerState.MAX_HUNGER);
            validateRange("hunger drain progress", hungerProgress, 0,
                    HungerState.NORMAL_DRAIN_TICKS - 1);
            validateRange("hydration", hydration, HydrationState.MIN_HYDRATION,
                    HydrationState.MAX_HYDRATION);
            validateRange("hydration drain progress", hydrationProgress, 0,
                    HydrationState.NORMAL_DRAIN_TICKS - 1);

            if (input.read() != -1) {
                throw new IOException("Unexpected trailing data in Rustank needs file");
            }

            return Optional.of(new SurvivalNeedsState.Snapshot(
                    new HungerState.Snapshot(hunger, hungerProgress),
                    new HydrationState.Snapshot(hydration, hydrationProgress)));
        } catch (java.io.EOFException exception) {
            throw new IOException("Truncated Rustank needs file", exception);
        }
    }

    private static void validateRange(String label, int value, int minimum, int maximum)
            throws IOException {
        if (value < minimum || value > maximum) {
            throw new IOException("Invalid " + label + " in Rustank needs file: " + value);
        }
    }
}
