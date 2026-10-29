package model;

public class CurrentAccount extends Account {
    private static final long serialVersionUID = 1L;
    private final long overdraftLimit;

    public CurrentAccount(String ownerName, long openingBalance, long overdraftLimit) {
        super(ownerName, openingBalance);
        this.overdraftLimit = overdraftLimit;
    }

    public CurrentAccount(CurrentAccount other) {
        super(other);
        this.overdraftLimit = other.overdraftLimit;
    }

    public long getOverdraftLimit() {
        return overdraftLimit;
    }

    @Override
    public double interestRate() {
        return 0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return getBalance() - amount >= -overdraftLimit;
    }

    @Override
    protected long minimumAllowedBalance() {
        return -overdraftLimit;
    }
}
