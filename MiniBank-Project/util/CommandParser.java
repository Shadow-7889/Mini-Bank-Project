package util;

import model.Command;
import model.TransactionType;

public final class CommandParser {
    private CommandParser() {
    }

    /** Turns "DEPOSIT AC0001 500" into a Command. */
    public static Command parse(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Command line is empty");
        }
        String[] parts = line.trim().split("\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Expected 3 parts (TYPE ACCOUNT AMOUNT) but found " + parts.length);
        }
        TransactionType type = TransactionType.valueOf(parts[0].toUpperCase());
        long amount = Long.parseLong(parts[2]);
        return new Command(type, parts[1], amount);
    }
}
