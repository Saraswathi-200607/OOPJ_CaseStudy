package com.banking.dao;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.util.AccountNumberGenerator;
import com.banking.util.DatabaseConnection;
 
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }
 
    // ── Account Operations ─────────────────────────────────────────
 
    /**
     * Creates a new account for the given user.
     *
     * @return the created Account, or null on failure
     */
    public synchronized Account createAccount(int userId, Account.AccountType type) throws SQLException {
        String accNumber = AccountNumberGenerator.generate();
        String sql = "INSERT INTO accounts (user_id, account_number, account_type, balance) VALUES (?, ?, ?, 0.00)";
 
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, accNumber);
            ps.setString(3, type.name());
            ps.executeUpdate();
 
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    Account acc = new Account(userId, accNumber, type);
                    acc.setAccountId(rs.getInt(1));
                    acc.setBalance(BigDecimal.ZERO);
                    return acc;
                }
            }
        }
        return null;
    }
 
    /**
     * Returns all accounts belonging to a user.
     */
    public synchronized List<Account> getAccountsByUser(int userId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE user_id = ? AND is_active = TRUE";
 
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) accounts.add(mapAccount(rs));
            }
        }
        return accounts;
    }
 
    /**
     * Finds an account by its account number.
     */
    public synchronized Account findByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ? AND is_active = TRUE";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapAccount(rs);
            }
        }
        return null;
    }
 
    // ── Financial Operations ───────────────────────────────────────
 
    /**
     * Deposits money into an account.
     * Uses a DB transaction to ensure atomicity.
     */
    public synchronized boolean deposit(int accountId, BigDecimal amount, String description) throws SQLException {
        Connection conn = getConn();
        boolean autoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
 
            // Lock the row for update
            BigDecimal newBalance = fetchAndLockBalance(conn, accountId).add(amount);
            updateBalance(conn, accountId, newBalance);
            insertTransaction(conn, accountId, Transaction.TransactionType.DEPOSIT, amount, newBalance, description, null);
 
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }
 
    /**
     * Withdraws money from an account.
     * Throws IllegalArgumentException if balance is insufficient.
     */
    public synchronized boolean withdraw(int accountId, BigDecimal amount, String description) throws SQLException {
        Connection conn = getConn();
        boolean autoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
 
            BigDecimal currentBalance = fetchAndLockBalance(conn, accountId);
            if (currentBalance.compareTo(amount) < 0) {
                conn.rollback();
                throw new IllegalArgumentException("Insufficient balance. Available: ₹" + currentBalance);
            }
 
            BigDecimal newBalance = currentBalance.subtract(amount);
            updateBalance(conn, accountId, newBalance);
            insertTransaction(conn, accountId, Transaction.TransactionType.WITHDRAWAL, amount, newBalance, description, null);
 
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }
 
    /**
     * Transfers funds between two accounts atomically.
     * Both debit and credit happen in a single DB transaction.
     */
    public synchronized boolean transfer(int fromAccountId, String toAccountNumber,
                                         BigDecimal amount, String description) throws SQLException {
        Connection conn = getConn();
        boolean autoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
 
            // Verify destination account
            Account toAccount = findByAccountNumber(toAccountNumber);
            if (toAccount == null) {
                conn.rollback();
                throw new IllegalArgumentException("Destination account not found: " + toAccountNumber);
            }
 
            // Debit source
            BigDecimal fromBalance = fetchAndLockBalance(conn, fromAccountId);
            if (fromBalance.compareTo(amount) < 0) {
                conn.rollback();
                throw new IllegalArgumentException("Insufficient balance. Available: ₹" + fromBalance);
            }
            BigDecimal newFromBalance = fromBalance.subtract(amount);
            updateBalance(conn, fromAccountId, newFromBalance);
            insertTransaction(conn, fromAccountId, Transaction.TransactionType.TRANSFER_OUT,
                    amount, newFromBalance, description, toAccountNumber);
 
            // Credit destination
            BigDecimal toBalance = fetchAndLockBalance(conn, toAccount.getAccountId());
            BigDecimal newToBalance = toBalance.add(amount);
            updateBalance(conn, toAccount.getAccountId(), newToBalance);
            insertTransaction(conn, toAccount.getAccountId(), Transaction.TransactionType.TRANSFER_IN,
                    amount, newToBalance, "Transfer from account", getAccountNumberById(conn, fromAccountId));
 
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }
 
    // ── Transaction History ────────────────────────────────────────
 
    /**
     * Retrieves the last N transactions for an account.
     */
    public synchronized List<Transaction> getTransactionHistory(int accountId, int limit) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC LIMIT ?";
 
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapTransaction(rs));
            }
        }
        return list;
    }
 
    // ── Private Helpers ────────────────────────────────────────────
 
    private BigDecimal fetchAndLockBalance(Connection conn, int accountId) throws SQLException {
        String sql = "SELECT balance FROM accounts WHERE account_id = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("balance");
                throw new SQLException("Account not found: " + accountId);
            }
        }
    }
 
    private void updateBalance(Connection conn, int accountId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }
 
    private void insertTransaction(Connection conn, int accountId,
                                   Transaction.TransactionType type, BigDecimal amount,
                                   BigDecimal balanceAfter, String description,
                                   String referenceAccount) throws SQLException {
        String sql = "INSERT INTO transactions (account_id, transaction_type, amount, balance_after, description, reference_account) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setString(2, type.name());
            ps.setBigDecimal(3, amount);
            ps.setBigDecimal(4, balanceAfter);
            ps.setString(5, description);
            ps.setString(6, referenceAccount);
            ps.executeUpdate();
        }
    }
 
    private String getAccountNumberById(Connection conn, int accountId) throws SQLException {
        String sql = "SELECT account_number FROM accounts WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("account_number") : "Unknown";
            }
        }
    }
 
    private Account mapAccount(ResultSet rs) throws SQLException {
        Account a = new Account();
        a.setAccountId(rs.getInt("account_id"));
        a.setUserId(rs.getInt("user_id"));
        a.setAccountNumber(rs.getString("account_number"));
        a.setAccountType(Account.AccountType.valueOf(rs.getString("account_type")));
        a.setBalance(rs.getBigDecimal("balance"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        a.setActive(rs.getBoolean("is_active"));
        return a;
    }
 
    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getInt("transaction_id"));
        t.setAccountId(rs.getInt("account_id"));
        t.setTransactionType(Transaction.TransactionType.valueOf(rs.getString("transaction_type")));
        t.setAmount(rs.getBigDecimal("amount"));
        t.setBalanceAfter(rs.getBigDecimal("balance_after"));
        t.setDescription(rs.getString("description"));
        t.setReferenceAccount(rs.getString("reference_account"));
        t.setTransactionDate(rs.getTimestamp("transaction_date"));
        return t;
    }

}
