# MiniBank

A console banking application written in Java. It runs in the terminal and lets you
open accounts, deposit, withdraw and transfer money, with input validation, error
handling, file persistence and thread-safe processing built in.

## Features

- **Menu-driven interface** built with an `enum`, a `record` header and a switch expression
- **Account types:** savings, current and fixed deposit, each with its own interest rate and withdrawal rules
- **Encapsulated model:** balances change only through `deposit()` and `withdraw()`
- **Validation:** regular expressions for mobile number, email, PAN and IFSC, plus a command parser (e.g. `DEPOSIT AC0001 500`)
- **Custom exceptions:** `InsufficientFundsException`, `InvalidAmountException`, `AccountNotFoundException`
- **Custom annotations** (`@Id`, `@Positive`, `@MaxLength`) checked at run time with reflection
- **Concurrency:** synchronized deposits, a thread pool, a producer-consumer buffer, and deadlock avoidance through consistent lock ordering
- **Persistence:** accounts saved and loaded with serialization, a transaction log, and an end-of-day report using NIO
- **Collections and generics:** a `HashMap` account store with sorting, and a generic `Repository<T>`

## Requirements

- JDK 17 or newer (a full JDK, not only a JRE, because the build needs `javac` and `jar`)

## Project structure

```
src/
  MiniBank.java        entry point and menu
  model/               accounts, customer, records, enums, interfaces
  model/annotation/    custom annotations
  exception/           custom exception classes
  util/                validators, command parser, statement formatter
  service/             store, repository, persistence, logging, threads, reports
build.sh               compiles the code and builds the JAR
manifest.mf            JAR manifest (Main-Class: MiniBank)
```

## Build

On Linux, macOS or Git Bash:

```
sh build.sh
```

Or manually:

```
mkdir out
javac -d out $(find src -name "*.java")
jar cfm minibank.jar manifest.mf -C out .
```

On Windows PowerShell:

```
mkdir out
javac -d out (Get-ChildItem -Recurse -Filter *.java src).FullName
jar cfm minibank.jar manifest.mf -C out .
```

## Run

Start the interactive menu:

```
java -jar minibank.jar
```

| Option | Action |
|---|---|
| 1. OPEN_ACCOUNT | Create a savings, current or fixed deposit account |
| 2. DEPOSIT | Add money to an account |
| 3. WITHDRAW | Take money out, subject to the account type's rules |
| 4. TRANSFER | Move money between two accounts |
| 5. EXIT | Quit the program |

Run the built-in demonstrations of the individual features (validation, polymorphism,
exceptions, threads, persistence, collections and so on):

```
java -jar minibank.jar demo
```

## Generated files

While running, the program may create a `data/` folder (saved accounts, logs and reports).
It is listed in `.gitignore` and should not be committed.
