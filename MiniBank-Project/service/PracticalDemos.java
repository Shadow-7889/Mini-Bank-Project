package service;

import static util.Validator.*; // Practical 6: static import (use the validators without the class name)

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.Account;
import model.BankInfo;
import model.Command;
import model.CurrentAccount;
import model.Customer;
import model.FixedDepositAccount;
import model.MenuOption;
import model.Premium;
import model.SavingsAccount;
import model.TransactionType;
import util.AnnotationValidator;
import util.AuditSession;
import util.CommandParser;
import util.StatementFormatter;

/**
 * One demo section per practical (1 to 13). Run with:  java -jar minibank.jar demo
 * These are the "In main, ..." steps of every Part B, kept together so each can be shown to the faculty.
 */
public final class PracticalDemos {
    private PracticalDemos() {
    }

    /** An action that may throw; lets us print "ok" or the failure message in one line. */
    private interface Action {
        void run() throws Exception;
    }

    private static void attempt(String label, Action action) {
        try {
            action.run();
            System.out.println("  " + label + " -> ok");
        } catch (BankException e) {
            System.out.println("  " + label + " -> " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  " + label + " -> " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private static void title(String text) {
        System.out.println();
        System.out.println("=== " + text + " ===");
    }

    public static void runAll() throws Exception {
        practical1();
        practical2();
        practical3();
        practical4();
        practical5();
        practical6();
        practical7();
        practical8();
        practical9();
        practical10();
        practical11();
        practical12();
        practical13();
        System.out.println();
        System.out.println("All MiniBank demos finished.");
    }

    // ------------------------------------------------------------------ P1
    private static void practical1() {
        title("Practical 1: BankInfo record and MenuOption enum");
        System.out.println(new BankInfo("CHARUSAT MiniBank", "Changa Branch"));
        for (MenuOption option : MenuOption.values()) {
            System.out.println("  " + (option.ordinal() + 1) + ". " + option);
        }
    }

    // ------------------------------------------------------------------ P2
    private static void practical2() {
        title("Practical 2: Customer, Account and encapsulation");
        Customer customer = new Customer("Riya", "riya@example.com", "9876543210");
        System.out.println("  Customer id: " + customer.getCustomerId());

        Account[] accounts = {
                new SavingsAccount("Riya", 5000),
                new CurrentAccount("Amit", 2000, 1000),
                new FixedDepositAccount("Neha", 10000)
        };
        Account riya = accounts[0];
        System.out.println("  Opening balance: " + riya.getBalance());
        attempt("deposit(2000)", () -> riya.deposit(2000));
        System.out.println("  Balance: " + riya.getBalance() + "   (expected 7000)");
        attempt("withdraw(3000)", () -> riya.withdraw(3000));
        System.out.println("  Balance: " + riya.getBalance() + "   (expected 4000)");
        attempt("withdraw(10000)", () -> riya.withdraw(10000));
        System.out.println("  Balance: " + riya.getBalance() + "   (unchanged, 4000)");

        attempt("Amit withdraw(2500) using overdraft", () -> accounts[1].withdraw(2500));
        attempt("Neha withdraw(100) from locked FD", () -> accounts[2].withdraw(100));
        for (Account a : accounts) {
            System.out.println("  " + a.getAccountNumber() + " balance = " + a.getBalance());
        }
    }

    // ------------------------------------------------------------------ P3
    private static void practical3() {
        title("Practical 3: toString, equals, hashCode, nested Address, clone, instanceof");
        SavingsAccount a1 = new SavingsAccount("Riya", 4000);
        Account a2 = new SavingsAccount(a1); // copy constructor: same account number
        System.out.println("  a1 prints as : " + a1);
        System.out.println("  a1.equals(a2): " + a1.equals(a2) + "   same hashCode: " + (a1.hashCode() == a2.hashCode()));
        Object obj = a1;
        System.out.println("  obj instanceof Account: " + (obj instanceof Account));
        System.out.println("  obj instanceof String : " + (obj instanceof String));

        Customer.Address address = new Customer.Address("12 MG Road", "Anand", "388001");
        Customer customer = new Customer("Riya", "riya@example.com", "9876543210", address);
        Customer copy = customer.clone();
        System.out.println("  Customer: " + customer + " | " + customer.getAddress());
        System.out.println("  clone is a different object: " + (copy != customer)
                + ", same id: " + copy.getCustomerId().equals(customer.getCustomerId())
                + ", own Address copy: " + (copy.getAddress() != customer.getAddress()));
    }

    // ------------------------------------------------------------------ P4
    private static void practical4() {
        title("Practical 4: Validator, CommandParser, StatementFormatter");
        System.out.println("  isValidMobile(\"9876543210\") = " + isValidMobile("9876543210"));
        System.out.println("  isValidMobile(\"12345\")      = " + isValidMobile("12345"));
        System.out.println("  isValidEmail(\"abc@xyz.com\") = " + isValidEmail("abc@xyz.com"));
        System.out.println("  isValidEmail(\"abc@\")        = " + isValidEmail("abc@"));
        System.out.println("  isValidPan(\"ABCDE1234F\")    = " + isValidPan("ABCDE1234F"));
        System.out.println("  isValidPan(\"abcde1234\")     = " + isValidPan("abcde1234"));
        System.out.println("  isValidIfsc(\"SBIN0001234\")  = " + isValidIfsc("SBIN0001234"));
        System.out.println("  isValidIfsc(\"SBIN1001234\")  = " + isValidIfsc("SBIN1001234"));
        System.out.println("  isValidAmount(\"500\") = " + isValidAmount("500") + ", isValidAmount(\"-5\") = " + isValidAmount("-5"));

        Command command = CommandParser.parse("DEPOSIT AC0001 500");
        System.out.println("  type=" + command.type() + ", accountNumber=" + command.accountNumber()
                + ", amount=" + command.amount());
        attempt("parse(\"DEPOSIT AC0001\")", () -> CommandParser.parse("DEPOSIT AC0001"));

        System.out.println(StatementFormatter.buildStatement(new SavingsAccount("Riya", 4000)));
    }

    // ------------------------------------------------------------------ P5
    private static void practical5() {
        title("Practical 5: abstract Account and run-time polymorphism");
        Account[] accounts = {
                new SavingsAccount("Riya", 5000, 1000),
                new CurrentAccount("Amit", 2000, 1000),
                new FixedDepositAccount("Neha", 10000)
        };
        for (Account a : accounts) {
            System.out.println("  " + a.getClass().getSimpleName() + ": interestRate() = " + a.interestRate()
                    + ", canWithdraw(3500) = " + a.canWithdraw(3500));
            if (a instanceof SavingsAccount savings) { // pattern-matching instanceof
                System.out.println("    (savings only) minimum balance = " + savings.getMinBalance());
            }
        }
    }

    // ------------------------------------------------------------------ P6
    private static void practical6() {
        title("Practical 6: interfaces, functional interface, marker interface, static import");
        WithdrawRule anonymous = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.isActive() && amount <= 10000;
            }
        };
        WithdrawRule lambda = (account, amount) -> account.isActive() && amount <= 10000;

        Account account = new SavingsAccount("Riya", 50000);
        System.out.println("  allow 5000  -> anonymous: " + anonymous.allow(account, 5000) + ", lambda: " + lambda.allow(account, 5000));
        System.out.println("  allow 20000 -> anonymous: " + anonymous.allow(account, 20000) + ", lambda: " + lambda.allow(account, 20000));

        Account fd = new FixedDepositAccount("Neha", 10000);
        System.out.println("  FD yearlyInterest()  = " + fd.yearlyInterest());
        System.out.println("  FD monthlyInterest() = " + fd.monthlyInterest());
        System.out.println("  FD is Premium (marker interface): " + (fd instanceof Premium));
        System.out.println("  Savings is Premium: " + (account instanceof Premium));
        System.out.println("  isValidMobile via static import: " + isValidMobile("9876543210"));
    }

    // ------------------------------------------------------------------ P7
    private static void practical7() {
        title("Practical 7: custom annotations + reflection");
        Account bad = new SavingsAccount("Riya", -100);
        System.out.println("  Account with balance -100:");
        for (String error : AnnotationValidator.validate(bad)) {
            System.out.println("    error: " + error);
        }
        Account good = new SavingsAccount("Riya", 5000);
        System.out.println("  Valid account -> errors found: " + AnnotationValidator.validate(good).length);

        Account longName = new SavingsAccount("A very very long owner name that is too long", 100);
        for (String error : AnnotationValidator.validateWithNames(longName)) {
            System.out.println("  with field name: " + error);
        }
    }

    // ------------------------------------------------------------------ P8
    private static void practical8() {
        title("Practical 8: custom exceptions, try-catch-finally, try-with-resources");
        Account account = new SavingsAccount("Riya", 1000);
        try {
            account.withdraw(5000);
        } catch (InsufficientFundsException e) {
            System.out.println("  Withdrawal failed: short by " + e.getShortfall() + ", balance stays " + account.getBalance());
        } catch (InvalidAmountException e) {
            System.out.println("  " + e.getMessage());
        } finally {
            System.out.println("  finally block: operation finished");
        }

        attempt("deposit(-50)", () -> account.deposit(-50));
        attempt("withdraw(0)", () -> account.withdraw(0));
        attempt("store.get(\"AC9999\")", () -> new AccountStore().get("AC9999"));

        Account other = new SavingsAccount("Amit", 0);
        attempt("transfer 400 to Amit", () -> account.transfer(other, 400));
        attempt("transfer 9000 to Amit", () -> account.transfer(other, 9000));

        try (AuditSession session = new AuditSession("demo")) {
            session.record("about to fail");
            throw new IllegalStateException("simulated error");
        } catch (IllegalStateException e) {
            System.out.println("  caught after the resource was closed: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ P9
    private static void practical9() throws InterruptedException {
        title("Practical 9: threads, race condition and synchronized");
        System.out.println("  10 threads x 1000 deposits of 1 -> correct total is 10000");

        Account unsafe = new SavingsAccount("Race", 0);
        runWorkers(unsafe, false);
        System.out.println("  Balance WITHOUT synchronization = " + unsafe.getBalance() + "  (wrong, changes every run)");

        Account safe = new SavingsAccount("Safe", 0);
        runWorkers(safe, true);
        System.out.println("  Balance WITH synchronized       = " + safe.getBalance() + "  (always 10000)");
    }

    private static void runWorkers(Account account, boolean safe) throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new AccountWorker(account, 1000, 1, safe), "worker-" + (i + 1));
        }
        System.out.println("  Thread state before start: " + threads[0].getState());
        for (Thread t : threads) {
            t.start();
        }
        System.out.println("  Thread state after start : " + threads[0].getState());
        for (Thread t : threads) {
            t.join();
        }
        System.out.println("  Thread state after join  : " + threads[0].getState());
    }

    // ------------------------------------------------------------------ P10
    private static void practical10() throws InterruptedException {
        title("Practical 10: thread pool, producer-consumer, deadlock");

        System.out.println("  -- thread pool of 4 threads, 12 tasks --");
        TransactionProcessor processor = new TransactionProcessor();
        Account pooled = new SavingsAccount("Pool", 1000);
        processor.register(pooled);
        for (int i = 0; i < 10; i++) {
            processor.submit(processor.task(new Command(TransactionType.DEPOSIT, pooled.getAccountNumber(), 100)));
        }
        for (int i = 0; i < 2; i++) {
            processor.submit(processor.task(new Command(TransactionType.WITHDRAW, pooled.getAccountNumber(), 50)));
        }
        processor.stop();
        System.out.println("  Final balance = " + pooled.getBalance() + "  (1000 + 10x100 - 2x50 = 1900)");

        System.out.println("  -- producer / consumer with wait() and notifyAll() --");
        TransactionProcessor worker = new TransactionProcessor();
        Account target = new SavingsAccount("Buffer", 0);
        worker.register(target);
        String no = target.getAccountNumber();
        String[] lines = {
                "DEPOSIT " + no + " 500", "DEPOSIT " + no + " 300", "WITHDRAW " + no + " 200",
                "DEPOSIT " + no + " 100", "WITHDRAW " + no + " 50", "DEPOSIT " + no + " 25"
        };
        TransactionBuffer buffer = new TransactionBuffer(3);
        Thread producer = new Thread(() -> {
            try {
                for (String line : lines) {
                    buffer.put(CommandParser.parse(line));
                    System.out.println("  produced: " + line);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "producer");
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < lines.length; i++) {
                    Command command = buffer.take();
                    worker.apply(command);
                    System.out.println("  consumed: " + command);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "consumer");
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        worker.stop();
        System.out.println("  Final balance = " + target.getBalance() + "  (500+300-200+100-50+25 = 675)");

        System.out.println("  -- deadlock: opposite lock order --");
        Account x = new SavingsAccount("X", 1000);
        Account y = new SavingsAccount("Y", 1000);
        boolean finished = DeadlockDemo.run(x, y, false);
        System.out.println("  Finished normally? " + finished);

        System.out.println("  -- fixed: smaller account number is always locked first --");
        Account p = new SavingsAccount("P", 1000);
        Account q = new SavingsAccount("Q", 1000);
        finished = DeadlockDemo.run(p, q, true);
        System.out.println("  Finished normally? " + finished + ", balances: P=" + p.getBalance() + ", Q=" + q.getBalance()
                + "  (expected 950 and 1050)");
    }

    // ------------------------------------------------------------------ P11
    private static void practical11() throws IOException, ClassNotFoundException {
        title("Practical 11: serialization, transaction log, NIO report");
        Path dataDir = Path.of("data");
        Path file = dataDir.resolve("accounts.dat");

        Account[] original = {
                new SavingsAccount("Riya", 5000),
                new CurrentAccount("Amit", 2000, 1000),
                new FixedDepositAccount("Neha", 9000)
        };
        StatePersister.save(original, file);
        Account[] loaded = StatePersister.load(file);
        System.out.println("  Saved " + original.length + " accounts to " + file + ", reloaded " + loaded.length + ":");
        for (int i = 0; i < loaded.length; i++) {
            System.out.println("    " + loaded[i] + "   (" + loaded[i].getClass().getSimpleName()
                    + ", equals original: " + loaded[i].equals(original[i]) + ")");
        }

        Path logs = dataDir.resolve("logs");
        clearFolder(logs);
        TransactionLog.append(logs.resolve("day1.log"), "DEPOSIT AC0001 500");
        TransactionLog.append(logs.resolve("day1.log"), "WITHDRAW AC0002 200");
        TransactionLog.append(logs.resolve("day2.log"), "DEPOSIT AC0003 1000");
        TransactionLog.append(logs.resolve("day2.log"), "WITHDRAW AC0001 300");
        Files.createFile(logs.resolve("empty.log")); // will be skipped
        Files.writeString(logs.resolve("notes.txt"), "not a log file"); // will be skipped
        System.out.println(ReportGenerator.generate(logs, dataDir.resolve("report.txt")));
        System.out.println("  (expected: deposits 1500, withdrawals 500, net 1000; report saved in data/report.txt)");
    }

    private static void clearFolder(Path folder) throws IOException {
        if (Files.exists(folder)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
                for (Path p : stream) {
                    Files.delete(p);
                }
            }
        }
    }

    // ------------------------------------------------------------------ P12
    private static void practical12() throws AccountNotFoundException {
        title("Practical 12: AccountStore, sorting, ConcurrentHashMap");
        AccountStore store = new AccountStore();
        Account a = new SavingsAccount("Riya", 5000);
        Account b = new SavingsAccount("Amit", 2000);
        Account c = new SavingsAccount("Neha", 9000);
        store.add(a);
        store.add(b);
        store.add(c);

        System.out.println("  get(" + b.getAccountNumber() + ") -> " + store.get(b.getAccountNumber()));
        System.out.println("  Sorted by balance (highest first):");
        for (Account account : store.sortedByBalance()) {
            System.out.println("    " + account);
        }
        System.out.println("  Balance above 3000: " + store.withBalanceAbove(3000));
        java.util.ArrayList<Account> natural = store.getAll();
        java.util.Collections.sort(natural); // uses Account.compareTo (by account number)
        System.out.println("  Natural order (Comparable, by account number): " + natural);
        System.out.println("  History: " + store.getHistory());
        System.out.println("  TransactionProcessor keeps its shared accounts in a ConcurrentHashMap (see Practical 10).");
    }

    // ------------------------------------------------------------------ P13
    private static void practical13() {
        title("Practical 13: generic Repository<T>, @Id by reflection, bounded types");
        Repository<Account> repository = new Repository<>();
        Account first = repository.save(new SavingsAccount("Riya", 5000));
        repository.saveAll(new CurrentAccount("Amit", 2000, 500), new FixedDepositAccount("Neha", 9000));

        System.out.println("  Stored " + repository.count() + " accounts. findById(" + first.getAccountNumber() + ") -> "
                + repository.findById(first.getAccountNumber()));
        System.out.println("  findAll():");
        for (Account account : repository.findAll()) {
            System.out.println("    " + account);
        }
        System.out.println("  delete(" + first.getAccountNumber() + ") -> " + repository.delete(first.getAccountNumber())
                + ", count now " + repository.count());

        Repository<Customer> customers = new Repository<>(); // same class, different type
        Customer customer = customers.save(new Customer("Riya", "riya@example.com", "9876543210"));
        System.out.println("  Repository<Customer>.findById(" + customer.getCustomerId() + ") -> "
                + customers.findById(customer.getCustomerId()));

        Integer[] ints = {1, 2, 3, 4};
        Double[] doubles = {2.5, 3.5};
        Long[] longs = {10L, 20L, 30L};
        System.out.println("  average(Integer[]) = " + Repository.average(ints)
                + ", average(Double[]) = " + Repository.average(doubles)
                + ", average(Long[]) = " + Repository.average(longs));
    }
}
