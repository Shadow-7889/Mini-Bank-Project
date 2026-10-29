package service;

import exception.InvalidAmountException;
import model.Account;

/** Practical 9: each worker thread deposits the same amount many times into one shared account. */
public class AccountWorker implements Runnable {
    private final Account account;
    private final int times;
    private final long amount;
    private final boolean safe;

    public AccountWorker(Account account, int times, long amount, boolean safe) {
        this.account = account;
        this.times = times;
        this.amount = amount;
        this.safe = safe;
    }

    public AccountWorker(Account account, int times, long amount) {
        this(account, times, amount, true);
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        System.out.println("  " + name + " started");
        for (int i = 0; i < times; i++) {
            if (safe) {
                try {
                    account.deposit(amount); // synchronized
                } catch (InvalidAmountException e) {
                    System.out.println("  " + name + ": " + e.getMessage());
                    return;
                }
            } else {
                account.depositUnsafe(amount); // not synchronized -> race condition
            }
        }
        System.out.println("  " + name + " finished");
    }
}
