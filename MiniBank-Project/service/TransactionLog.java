package service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** Practical 11: append-only transaction log. */
public final class TransactionLog {
    private TransactionLog() {
    }

    public static void append(Path file, String line) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        byte[] bytes = (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8);
        Files.write(file, bytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
