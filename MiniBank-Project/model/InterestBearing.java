package model;

/** Practical 6: default methods use the two abstract ones. */
public interface InterestBearing {
    double interestRate();

    long getBalance();

    default double yearlyInterest() {
        return getBalance() * interestRate() / 100.0;
    }

    // second default method (supplementary problem)
    default double monthlyInterest() {
        return yearlyInterest() / 12.0;
    }
}
