package util;

import model.Account;

public final class StatementFormatter {
    private StatementFormatter() {
    }

    public static String buildStatement(Account account) {
        StringBuilder sb = new StringBuilder();
        sb.append("========== ACCOUNT STATEMENT ==========\n");
        sb.append("Account No    : ").append(account.getAccountNumber()).append('\n');
        sb.append("Owner         : ").append(account.getOwnerName()).append('\n');
        sb.append("Type          : ").append(account.getClass().getSimpleName()).append('\n');
        sb.append("Balance       : ").append(account.getBalance()).append('\n');
        sb.append("Status        : ").append(account.isActive() ? "ACTIVE" : "INACTIVE").append('\n');
        sb.append("Interest rate : ").append(account.interestRate()).append("%\n");
        sb.append("=======================================");
        return sb.toString();
    }
}
