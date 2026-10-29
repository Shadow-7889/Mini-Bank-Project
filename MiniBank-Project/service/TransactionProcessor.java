package service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import exception.AccountNotFoundException;
import exception.BankException;
import model.Account;
import model.Command;

/** Practical 10 (thread pool) + Practical 12 (ConcurrentHashMap shared by all pool threads). */
public class TransactionProcessor {
    private final ExecutorService pool = Executors.newFixedThreadPool(4);
    private final ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();

    public void register(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public Account find(String accountNumber) throws AccountNotFoundException {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return account;
    }

    /** Runs one command on the matching account; failures are reported, never thrown. */
    public void apply(Command command) {
        try {
            Account account = find(command.accountNumber());
            switch (command.type()) {
                case DEPOSIT -> account.deposit(command.amount());
                case WITHDRAW -> account.withdraw(command.amount());
                case TRANSFER -> System.out.println("  TRANSFER needs two accounts - skipped");
            }
        } catch (BankException e) {
            System.out.println("  ! " + command + " failed: " + e.getMessage());
        }
    }

    /** Wraps a command in a Runnable task for the pool. */
    public Runnable task(Command command) {
        return () -> {
            apply(command);
            System.out.println("  " + Thread.currentThread().getName() + " executed " + command);
        };
    }

    public void submit(Runnable task) {
        pool.execute(task);
    }

    public void stop() throws InterruptedException {
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);
    }
}
