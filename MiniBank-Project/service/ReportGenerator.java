package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/** Practical 11: walks the logs folder with NIO and writes an end-of-day summary. */
public final class ReportGenerator {
    private ReportGenerator() {
    }

    public static String generate(Path logsDir, Path reportFile) throws IOException {
        long totalDeposits = 0;
        long totalWithdrawals = 0;
        StringBuilder sb = new StringBuilder("END-OF-DAY REPORT\n-----------------\n");

        // "*.log" glob: files that do not end with .log are skipped (supplementary problem)
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsDir, "*.log")) {
            for (Path file : stream) {
                BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                if (attrs.size() == 0) {
                    sb.append("Skipped empty file: ").append(file.getFileName()).append('\n');
                    continue;
                }
                sb.append("File: ").append(file.getFileName())
                        .append(" | size=").append(attrs.size()).append(" bytes")
                        .append(" | modified=").append(attrs.lastModifiedTime()).append('\n');

                try (BufferedReader reader = Files.newBufferedReader(file)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.trim().split("\\s+");
                        if (parts.length < 3) {
                            continue;
                        }
                        try {
                            long amount = Long.parseLong(parts[parts.length - 1]);
                            switch (parts[0].toUpperCase()) {
                                case "DEPOSIT" -> totalDeposits += amount;
                                case "WITHDRAW" -> totalWithdrawals += amount;
                                default -> { }
                            }
                        } catch (NumberFormatException e) {
                            // ignore a damaged line
                        }
                    }
                }
            }
        }

        sb.append("-----------------\n");
        sb.append("Total deposits    : ").append(totalDeposits).append('\n');
        sb.append("Total withdrawals : ").append(totalWithdrawals).append('\n');
        sb.append("Net change        : ").append(totalDeposits - totalWithdrawals).append('\n');

        Path parent = reportFile.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(reportFile, sb.toString());
        return sb.toString();
    }
}
