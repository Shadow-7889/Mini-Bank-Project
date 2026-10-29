package model;

/** The deposit is locked, so no withdrawal is ever allowed. It is also a Premium account (marker interface). */
public class FixedDepositAccount extends Account implements Premium {
    private static final long serialVersionUID = 1L;

    public FixedDepositAccount(String ownerName, long openingBalance) {
        super(ownerName, openingBalance);
    }

    public FixedDepositAccount(FixedDepositAccount other) {
        super(other);
    }

    @Override
    public double interestRate() {
        return 7.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return false;
    }

    @Override
    protected long minimumAllowedBalance() {
        return getBalance(); // it must stay exactly where it is
    }
}
