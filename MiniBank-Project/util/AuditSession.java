package util;

/** Practical 8: a small AutoCloseable used to show try-with-resources. */
public class AuditSession implements AutoCloseable {
    private final String name;

    public AuditSession(String name) {
        this.name = name;
        System.out.println("[audit] session '" + name + "' opened");
    }

    public void record(String message) {
        System.out.println("[audit] " + name + ": " + message);
    }

    @Override
    public void close() {
        System.out.println("[audit] session '" + name + "' closed");
    }
}
