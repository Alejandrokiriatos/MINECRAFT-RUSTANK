package com.rustank.survival;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SurvivalNeedsPersistenceTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void roundTripsBothNeedsAndPartialDrainTimers() throws IOException {
        SurvivalNeedsState.Snapshot snapshot = new SurvivalNeedsState.Snapshot(
                new HungerState.Snapshot(63, 875),
                new HydrationState.Snapshot(42, 1100));
        Path file = temporaryDirectory.resolve("players").resolve("player.rustank-needs");

        SurvivalNeedsPersistence.save(file, snapshot);

        assertEquals(Optional.of(snapshot), SurvivalNeedsPersistence.load(file));
    }

    @Test
    void missingFileMeansNewPlayer() throws IOException {
        Path file = temporaryDirectory.resolve("not-created.rustank-needs");
        assertTrue(SurvivalNeedsPersistence.load(file).isEmpty());
    }

    @Test
    void replacingAnExistingFileDoesNotLeaveOldValues() throws IOException {
        Path file = temporaryDirectory.resolve("player.rustank-needs");
        SurvivalNeedsState.Snapshot first = new SurvivalNeedsState.Snapshot(
                new HungerState.Snapshot(90, 10),
                new HydrationState.Snapshot(80, 20));
        SurvivalNeedsState.Snapshot second = new SurvivalNeedsState.Snapshot(
                new HungerState.Snapshot(30, 800),
                new HydrationState.Snapshot(15, 1000));

        SurvivalNeedsPersistence.save(file, first);
        SurvivalNeedsPersistence.save(file, second);

        assertEquals(Optional.of(second), SurvivalNeedsPersistence.load(file));
    }

    @Test
    void rejectsUnknownFileSignature() throws IOException {
        Path file = temporaryDirectory.resolve("bad-signature.rustank-needs");
        Files.write(file, new byte[] {0, 0, 0, 0, 0, 0, 0, 1});

        assertThrows(IOException.class, () -> SurvivalNeedsPersistence.load(file));
    }

    @Test
    void rejectsUnsupportedVersion() throws IOException {
        Path file = temporaryDirectory.resolve("bad-version.rustank-needs");
        try (DataOutputStream output = new DataOutputStream(Files.newOutputStream(file))) {
            output.writeInt(0x5253544B);
            output.writeInt(99);
        }

        assertThrows(IOException.class, () -> SurvivalNeedsPersistence.load(file));
    }

    @Test
    void rejectsTruncatedAndTrailingData() throws IOException {
        Path file = temporaryDirectory.resolve("bad-length.rustank-needs");
        try (DataOutputStream output = new DataOutputStream(Files.newOutputStream(file))) {
            output.writeInt(0x5253544B);
            output.writeInt(1);
            output.writeInt(50);
        }
        assertThrows(IOException.class, () -> SurvivalNeedsPersistence.load(file));

        SurvivalNeedsState.Snapshot snapshot = new SurvivalNeedsState().snapshot();
        SurvivalNeedsPersistence.save(file, snapshot);
        Files.write(file, new byte[] {1}, java.nio.file.StandardOpenOption.APPEND);
        assertThrows(IOException.class, () -> SurvivalNeedsPersistence.load(file));
    }

    @Test
    void rejectsOutOfRangeNeedValuesAndTimers() throws IOException {
        Path file = temporaryDirectory.resolve("bad-values.rustank-needs");
        try (DataOutputStream output = new DataOutputStream(Files.newOutputStream(file))) {
            output.writeInt(0x5253544B);
            output.writeInt(1);
            output.writeInt(101);
            output.writeInt(0);
            output.writeInt(100);
            output.writeInt(0);
        }
        assertThrows(IOException.class, () -> SurvivalNeedsPersistence.load(file));

        try (DataOutputStream output = new DataOutputStream(Files.newOutputStream(file))) {
            output.writeInt(0x5253544B);
            output.writeInt(1);
            output.writeInt(100);
            output.writeInt(0);
            output.writeInt(100);
            output.writeInt(HydrationState.NORMAL_DRAIN_TICKS);
        }
        assertThrows(IOException.class, () -> SurvivalNeedsPersistence.load(file));
    }

    @Test
    void refusesNullInputs() {
        assertThrows(IllegalArgumentException.class,
                () -> SurvivalNeedsPersistence.save(null, new SurvivalNeedsState().snapshot()));
        assertThrows(IllegalArgumentException.class,
                () -> SurvivalNeedsPersistence.save(temporaryDirectory.resolve("x"), null));
        assertThrows(IllegalArgumentException.class, () -> SurvivalNeedsPersistence.load(null));
    }
}
