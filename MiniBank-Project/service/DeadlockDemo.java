package service;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import exception.BankException;
import model.Account;

/** Practical 10: two threads move money A->B and B->A. Opposite lock order = deadlock; same order = fine. */
public final class DeadlockDemo {
    private DeadlockDemo() {
    }

    private static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void transfer(Account from, Account to, long amount, boolean ordered) {
        Account first = from;
        Account second = to;
        // The fix: always lock the account with the smaller account number first.
        if (ordered && from.getAccountNumber().compareTo(to.getAccountNumber()) > 0) {
            first = to;
            second = from;
        }
        synchronized (first) {
            pause(200); // make sure the other thread grabs its first lock too
            synchronized (second) {
                try {
                    from.withdraw(amount);
                    to.deposit(amount);
                    System.out.println("  " + Thread.currentThread().getName() + ": moved " + amount
                            + " from " + from.getAccountNumber() + " to " + to.getAccountNumber());
                } catch (BankException e) {
                    System.out.println("  " + Thread.currentThread().getName() + " failed: " + e.getMessage());
                }
            }
        }
    }

    /** Returns true if both threads finished, false if they are stuck (deadlock). */
    public static boolean run(Account a, Account b, boolean ordered) throws InterruptedException {
        Thread t1 = new Thread(() -> transfer(a, b, 100, ordered), "A-to-B");
        Thread t2 = new Thread(() -> transfer(b, a, 50, ordered), "B-to-A");
        t1.setDaemon(true); // daemon threads so a deadlock cannot keep the JVM alive
        t2.setDaemon(true);
        t1.start();
        t2.start();
        t1.join(2000);
        t2.join(2000);
        boolean finished = !t1.isAlive() && !t2.isAlive();
        if (!finished) {
            ThreadMXBean bean = ManagementFactory.getThreadMXBean();
            long[] ids = bean.findDeadlockedThreads();
            System.out.println("  DEADLOCK! Threads blocked forever: " + (ids == null ? 0 : ids.length));
        }
        return finished;
    }
}
