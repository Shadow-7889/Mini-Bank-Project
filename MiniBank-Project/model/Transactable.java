package model;

import exception.InsufficientFundsException;
import exception.InvalidAmountException;

/** Practical 6 (exceptions added in Practical 8). */
public interface Transactable {
    void deposit(long amount) throws InvalidAmountException;

    boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException;
}
