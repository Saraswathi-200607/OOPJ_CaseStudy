# 🏦 SecureBank — Java Desktop Banking Application
A fully functional **desktop banking application** built with Java Swing and MySQL. SecureBank supports user registration, secure login, account management, deposits, withdrawals, and fund transfers — all backed by atomic database transactions.
## 📸 Screenshots

> _Login Screen · Dashboard · Transaction History_

<img width="577" height="760" alt="image" src="https://github.com/user-attachments/assets/21fc1662-be0d-4555-8c9f-45c0f66b1090" />
<img width="1048" height="693" alt="image" src="https://github.com/user-attachments/assets/2b11a3d1-a1b4-4f42-835a-77afbe41fb2d" />


## ✨ Features

- 🔐 **Secure Authentication** — SHA-256 password hashing, no plain-text storage
- 👤 **User Registration** — with duplicate username/email detection
- 🏦 **Account Management** — Savings & Current account types
- 💰 **Deposit & Withdrawal** — with real-time balance updates
- 🔄 **Fund Transfers** — atomic double-entry between accounts
- 📜 **Transaction History** — paginated, ordered by latest
- 🛡️ **SQL Injection Prevention** — via PreparedStatements throughout
- 🔒 **Concurrency Safe** — row-level `SELECT FOR UPDATE` locking
- 🎨 **Clean Modern UI** — custom-styled Java Swing interface

## 🗂️ Project Structure

src/
└── com/banking/
    ├── Main.java                          # App entry point
    ├── model/
    │   ├── User.java                      # User entity
    │   ├── Account.java                   # Account entity (SAVINGS / CURRENT)
    │   └── Transaction.java               # Transaction entity
    ├── dao/
    │   ├── UserDAO.java                   # User DB operations
    │   └── AccountDAO.java                # Account & financial DB operations
    ├── util/
    │   ├── DatabaseConnection.java        # Singleton DB connection
    │   ├── PasswordUtil.java              # SHA-256 hashing
    │   └── AccountNumberGenerator.java    # Random account number generator
    └── ui/
        ├── LoginFrame.java                # Login & Registration screen
        └── DashboardFrame.java            # Main banking dashboard

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17+ |
| UI Framework | Java Swing |
| Database | MySQL 8.0+ |
| JDBC Driver | mysql-connector-j |
| Password Security | SHA-256 (java.security.MessageDigest) |
| Architecture | DAO Pattern + Singleton |

## ⚙️ Setup & Installation

### Prerequisites

- Java JDK 17 or higher
- MySQL 8.0 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, or NetBeans)
- `mysql-connector-j.jar` added to your classpath

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/securebank.git
cd securebank
```

### 2. Set Up the Database

Open MySQL and run the schema:

```sql
CREATE DATABASE online_banking;
USE online_banking;

CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(100)        NOT NULL,
    email         VARCHAR(100) UNIQUE NOT NULL,
    username      VARCHAR(50)  UNIQUE NOT NULL,
    password_hash VARCHAR(255)        NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active     BOOLEAN   DEFAULT TRUE
);

CREATE TABLE accounts (
    account_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_id        INT            NOT NULL,
    account_number VARCHAR(20)    UNIQUE NOT NULL,
    account_type   ENUM('SAVINGS','CURRENT') NOT NULL,
    balance        DECIMAL(15,2)  DEFAULT 0.00,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active      BOOLEAN   DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE transactions (
    transaction_id   INT AUTO_INCREMENT PRIMARY KEY,
    account_id       INT            NOT NULL,
    transaction_type ENUM('DEPOSIT','WITHDRAWAL','TRANSFER_IN','TRANSFER_OUT') NOT NULL,
    amount           DECIMAL(15,2)  NOT NULL,
    balance_after    DECIMAL(15,2)  NOT NULL,
    description      VARCHAR(255),
    reference_account VARCHAR(20),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);
```

### 3. Configure Database Credentials

Open `src/com/banking/util/DatabaseConnection.java` and update:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/online_banking";
private static final String USER     = "your_mysql_username";
private static final String PASSWORD = "your_mysql_password";
```

### 4. Add JDBC Driver

Download [mysql-connector-j](https://dev.mysql.com/downloads/connector/j/) and add the `.jar` to your project's classpath/build path.

### 5. Run the Application

Run `Main.java` — the app will verify the database connection before launching the UI.

## 🏗️ Architecture & Design Patterns

### Singleton — `DatabaseConnection`
A single JDBC connection is reused throughout the application lifetime. The `getInstance()` method is `synchronized` and auto-reconnects if the connection drops.

### DAO Pattern — `UserDAO` & `AccountDAO`
All SQL is isolated in the DAO layer. The UI never talks to the database directly — it only works with model objects returned by DAOs.

### Atomic Transactions
Every financial operation (deposit, withdraw, transfer) disables auto-commit, uses `SELECT ... FOR UPDATE` to lock the row, performs the operation, and commits — or rolls back on any failure. This prevents race conditions and partial updates.

## 🔐 Security Highlights

- Passwords are **never stored in plain text** — SHA-256 hashed before insertion
- All queries use **PreparedStatements** — immune to SQL injection
- Row-level **database locks** prevent concurrent balance manipulation
- Active flag on users and accounts supports **soft deletes**

---

## 📋 How to Use

1. **Register** — Fill in your name, email, username, and password on the Register tab
2. **Login** — Enter your credentials on the Login tab
3. **Create Account** — Choose Savings or Current account type from the dashboard
4. **Deposit** — Enter amount and an optional description
5. **Withdraw** — Enter amount; app prevents overdraft automatically
6. **Transfer** — Enter the destination account number and amount
7. **View History** — See your last transactions with full details

## 🤝 Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a new branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

## 👩‍💻 Author
Neelakantam Saraswathi
- GitHub: [Saraswathi-200607](https://github.com/Saraswathi-200607)
- LinkedIn: [ Saraswathi Govardhan](www.linkedin.com/in/saraswathi-govardhan-209544369)

> _SecureBank was built as a learning project demonstrating Java Swing UI, DAO architecture, and secure financial transaction handling with MySQL._
