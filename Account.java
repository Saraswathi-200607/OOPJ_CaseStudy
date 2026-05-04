package com.banking.model;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Account {
    public enum AccountType { SAVINGS, CURRENT }
    
    private int accountId;
    private int userId;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private Timestamp createdAt;
    private boolean active;
 
    public Account() {}
 
    public Account(int userId, String accountNumber, AccountType accountType) {
        this.userId        = userId;
        this.accountNumber = accountNumber;
        this.accountType   = accountType;
        this.balance       = BigDecimal.ZERO;
    }
 
    // ── Getters & Setters ──────────────────────────────────────────
 
    public int getAccountId()                    { return accountId; }
    public void setAccountId(int accountId)      { this.accountId = accountId; }
 
    public int getUserId()                   { return userId; }
    public void setUserId(int userId)        { this.userId = userId; }
 
    public String getAccountNumber()                     { return accountNumber; }
    public void setAccountNumber(String accountNumber)   { this.accountNumber = accountNumber; }
 
    public AccountType getAccountType()                      { return accountType; }
    public void setAccountType(AccountType accountType)      { this.accountType = accountType; }
 
    public BigDecimal getBalance()                   { return balance; }
    public void setBalance(BigDecimal balance)       { this.balance = balance; }
 
    public Timestamp getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)    { this.createdAt = createdAt; }
 
    public boolean isActive()               { return active; }
    public void setActive(boolean active)   { this.active = active; }
 
    @Override
    public String toString() {
        return accountNumber + " [" + accountType + "] - ₹" + balance;
    }

}
