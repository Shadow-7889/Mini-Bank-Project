package model;

import java.io.Serializable;
import java.util.Objects;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.annotation.Id;
import model.annotation.MaxLength;
import model.annotation.Positive;

/**
 * Practical 2 (encapsulation), 3 (toString/equals/hashCode), 5 (abstract),
 * 6 (interfaces), 7 (annotations), 8 (exceptions), 9 (synchronized),
 * 11 (Serializable) and 12 (Comparable).
 */
public abstract class Account implements Transactable, InterestBearing, Comparable<Account>, Serializable {
    private static final long serialVersionUID = 1L;

    private static int accountCounter = 0;

    @Id
    private final String accountNumber;
    @MaxLength(30)
    private final String ownerName;
    @Positive
    private long balance;
    private boolean active;

    public Account(String ownerName, long openingBalance) {
        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = openingBalance;
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    /** Copy constructor: makes a second object with the SAME account number (used to test equals). */
    protected Account(Account other) {
        this.accountNumber = other.accountNumber;
        this.ownerName = other.ownerName;
        this.balance = other.balance;
        this.active = other.active;
    }

    private static synchronized String generateAccountNumber() {
        accountCounter++;
        return String.format("AC%04d", accountCounter);
    }

    /** After loading saved accounts, make sure new accounts never reuse a loaded number. */
    public static synchronized void syncCounter(Account[] accounts) {
        for (Account a : accounts) {
            int n = Integer.parseInt(a.accountNumber.substring(2));
            if (n > accountCounter) {
                accountCounter = n;
            }
        }
    }

    // ---- rules every account type must define (Practical 5) ----
    @Override
    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);

    /** Lowest balance this account may reach; used to compute the shortfall. */
    protected long minimumAllowedBalance() {
        return 0;
    }

    // ---- money operations: the ONLY way the balance can change ----
    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        balance += amount;
    }

    /** NOT thread-safe on purpose: used only by the Practical 9 demo to show a race condition. */
    public void depositUnsafe(long amount) {
        long temp = balance;
        Thread.yield(); // gives other threads a chance to interfere
        balance = temp + amount;
    }

    @Override
    public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        if (!canWithdraw(amount)) {
            long shortfall = minimumAllowedBalance() - (balance - amount);
            throw new InsufficientFundsException(shortfall > 0 ? shortfall : amount);
        }
        balance -= amount;
        return true;
    }

    /** Practical 8: try / catch / finally, and rethrows the BankException. */
    public void transfer(Account to, long amount) throws BankException {
        try {
            withdraw(amount);
            to.deposit(amount);
            System.out.println("Transfer of " + amount + " from " + accountNumber + " to " + to.accountNumber + " succeeded");
        } catch (BankException e) {
            System.out.println("Transfer failed: " + e.getMessage());
            throw e;
        } finally {
            System.out.println("Transfer attempt " + accountNumber + " -> " + to.accountNumber + " finished");
        }
    }

    // ---- getters (no public setter for balance!) ----
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public synchronized long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // ---- Object methods (Practical 3) and ordering (Practical 12) ----
    @Override
    public String toString() {
        return accountNumber + " | " + ownerName + " | " + getBalance();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Account other)) {
            return false;
        }
        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public int compareTo(Account other) {
        return accountNumber.compareTo(other.accountNumber);
    }
}
