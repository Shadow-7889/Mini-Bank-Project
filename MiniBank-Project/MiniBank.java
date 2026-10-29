import java.util.Scanner;
import exception.BankException;
import model.Account;
import model.BankInfo;
import model.CurrentAccount;
import model.Customer;
import model.FixedDepositAccount;
import model.MenuOption;
import model.SavingsAccount;
import service.AccountStore;
import service.PracticalDemos;
import service.Repository;
import util.AnnotationValidator;
import util.Validator;
public class MiniBank {
    private static final AccountStore store = new AccountStore();
    private static final Repository<Customer> customers = new Repository<>();
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equalsIgnoreCase("demo")) {
            PracticalDemos.runAll();
            return;
        }

        BankInfo info = new BankInfo("CHARUSAT MiniBank", "Changa Branch");
        System.out.println(info);

        MenuOption[] options = MenuOption.values();
        boolean running = true;
        while (running) {
            printMenu(options);
            System.out.print("Enter your choice: ");
            if (!in.hasNextLine()) {
                break;
            }
            String text = in.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Please type a number from 1 to " + options.length + ".");
                continue; 
            }
            if (choice < 1 || choice > options.length) {
                System.out.println("Invalid choice. Please type a number from 1 to " + options.length + ".");
                continue;
            }

            MenuOption option = options[choice - 1];
            try {
                String message = switch (option) {
                    case OPEN_ACCOUNT -> openAccount();
                    case DEPOSIT -> deposit();
                    case WITHDRAW -> withdraw();
                    case TRANSFER -> transfer();
                    case EXIT -> {
                        running = false;
                        yield "Thank you for using " + info.name() +" 25CE052-Mann";
                    }
                };
                System.out.println(message);
            } catch (BankException e) {
                System.out.println("Operation failed: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("That is not a valid number. Nothing was changed.");
            }
        }
    }

    private static void printMenu(MenuOption[] options) {
        System.out.println();
        System.out.println("------ MENU ------");
        for (MenuOption option : options) {
            System.out.println((option.ordinal() + 1) + ". " + option);
        }
    }

    private static String read(String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine().trim() : "";
    }

    private static String openAccount() {
        String name = read("Owner name: ");
        if (name.isEmpty()) {
            return "Name cannot be empty.";
        }
        String mobile = read("Mobile (10 digits): ");
        if (!Validator.isValidMobile(mobile)) {
            return "Invalid mobile number.";
        }
        String email = read("Email: ");
        if (!Validator.isValidEmail(email)) {
            return "Invalid email address.";
        }
        String type = read("Account type (1 = Savings, 2 = Current, 3 = Fixed Deposit): ");
        String amountText = read("Opening balance: ");
        if (!Validator.isValidAmount(amountText)) {
            return "Opening balance must be a positive whole number.";
        }
        long opening = Long.parseLong(amountText);

        Account account = switch (type) {
            case "1" -> new SavingsAccount(name, opening);
            case "2" -> new CurrentAccount(name, opening, 10000);
            case "3" -> new FixedDepositAccount(name, opening);
            default -> null;
        };
        if (account == null) {
            return "Invalid account type.";
        }
        String[] problems = AnnotationValidator.validateWithNames(account);
        if (problems.length > 0) {
            return "Account rejected: " + String.join(", ", problems);
        }

        store.add(account);
        Customer customer = customers.save(new Customer(name, email, mobile));
        return "Account opened: " + account + "  (" + account.getClass().getSimpleName()
                + ", customer " + customer.getCustomerId() + ")";
    }

    private static String deposit() throws BankException {
        Account account = store.get(read("Account number: ").toUpperCase());
        long amount = Long.parseLong(read("Amount to deposit: "));
        account.deposit(amount);
        store.log("DEPOSIT " + account.getAccountNumber() + " " + amount);
        return "Deposited " + amount + ". New balance: " + account.getBalance();
    }

    private static String withdraw() throws BankException {
        Account account = store.get(read("Account number: ").toUpperCase());
        long amount = Long.parseLong(read("Amount to withdraw: "));
        account.withdraw(amount);
        store.log("WITHDRAW " + account.getAccountNumber() + " " + amount);
        return "Withdrew " + amount + ". New balance: " + account.getBalance();
    }

    private static String transfer() throws BankException {
        Account from = store.get(read("From account number: ").toUpperCase());
        Account to = store.get(read("To account number: ").toUpperCase());
        long amount = Long.parseLong(read("Amount to transfer: "));
        from.transfer(to, amount);
        store.log("TRANSFER " + from.getAccountNumber() + " " + to.getAccountNumber() + " " + amount);
        return "Balances now -> " + from.getAccountNumber() + ": " + from.getBalance()
                + ", " + to.getAccountNumber() + ": " + to.getBalance();
    }
}
