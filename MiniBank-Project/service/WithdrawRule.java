package service;

import model.Account;

/** Practical 6: functional interface (one abstract method), used with an anonymous class and a lambda. */
@FunctionalInterface
public interface WithdrawRule {
    boolean allow(Account account, long amount);
}
