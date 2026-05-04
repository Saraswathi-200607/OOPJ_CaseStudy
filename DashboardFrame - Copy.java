package com.banking.ui;
import com.banking.dao.AccountDAO;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.model.User;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
 

public class DashboardFrame extends JFrame{
    private static final Color BG_PAGE      = new Color(243, 246, 250);
    private static final Color BG_SIDEBAR   = new Color(25, 100, 195);    // blue sidebar
    private static final Color BG_CARD      = Color.WHITE;
    private static final Color BG_TABLE_ROW = new Color(249, 251, 253);
    private static final Color ACCENT       = new Color(25, 100, 195);
    private static final Color ACCENT_GREEN = new Color(22, 163,  74);
    private static final Color ACCENT_RED   = new Color(220,  38,  38);
    private static final Color ACCENT_AMBER = new Color(217, 119,   6);
    private static final Color TEXT_PRIMARY = new Color(30,  40,  60);
    private static final Color TEXT_WHITE   = Color.WHITE;
    private static final Color TEXT_MUTED   = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color SIDEBAR_HOVER = new Color(18,  78, 160);
    private static final Color HEADER_BG    = new Color(25, 100, 195);
 
    private final User       user;
    private final AccountDAO accountDAO = new AccountDAO();
 
    private Account            selectedAccount;
    private JLabel             balanceLabel, accountInfoLabel;
    private JComboBox<Account> accountSelector;
    private JTable             transactionTable;
    private DefaultTableModel  tableModel;
 
    public DashboardFrame(User user) {
        this.user = user;
        setTitle("SecureBank — Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 660);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_PAGE);
 
        add(buildSidebar(),  BorderLayout.WEST);
        add(buildMainArea(), BorderLayout.CENTER);
 
        loadUserAccounts();
    }
 
    // ── Sidebar ────────────────────────────────────────────────────
 
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(28, 16, 20, 16));
 
        JLabel logo = new JLabel("🏦  SecureBank");
        logo.setFont(new Font("Georgia", Font.BOLD, 17));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(CENTER_ALIGNMENT);
        sidebar.add(logo);
 
        sidebar.add(Box.createVerticalStrut(6));
        JSeparator sep1 = new JSeparator();
        sep1.setForeground(new Color(255, 255, 255, 60));
        sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sep1);
        sidebar.add(Box.createVerticalStrut(18));
 
        JLabel welcome = new JLabel("Hello, " + user.getFullName().split(" ")[0] + "!");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        welcome.setForeground(Color.WHITE);
        welcome.setAlignmentX(CENTER_ALIGNMENT);
        sidebar.add(welcome);
        sidebar.add(Box.createVerticalStrut(4));
 
        JLabel member = new JLabel("Member Account");
        member.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        member.setForeground(new Color(186, 214, 255));
        member.setAlignmentX(CENTER_ALIGNMENT);
        sidebar.add(member);
        sidebar.add(Box.createVerticalStrut(26));
 
        sidebar.add(makeSidebarBtn("💳   My Accounts",   () -> {}));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(makeSidebarBtn("➕   New Account",    this::openCreateAccountDialog));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(makeSidebarBtn("📥   Deposit",        this::openDepositDialog));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(makeSidebarBtn("📤   Withdraw",       this::openWithdrawDialog));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(makeSidebarBtn("🔄   Transfer",       this::openTransferDialog));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(makeSidebarBtn("📊   History",        this::refreshTransactionHistory));
 
        sidebar.add(Box.createVerticalGlue());
 
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(255, 255, 255, 60));
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sep2);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(makeSidebarBtn("🚪   Logout", this::logout));
 
        return sidebar;
    }
 
    // ── Main Area ──────────────────────────────────────────────────
 
    private JScrollPane buildMainArea() {
        JPanel main = new JPanel();
        main.setBackground(BG_PAGE);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(new EmptyBorder(28, 28, 28, 28));
 
        // ── Top bar ─────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG_PAGE);
        topBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        topBar.setAlignmentX(LEFT_ALIGNMENT);
 
        JLabel pageTitle = new JLabel("Dashboard Overview");
        pageTitle.setFont(new Font("Georgia", Font.BOLD, 20));
        pageTitle.setForeground(TEXT_PRIMARY);
        topBar.add(pageTitle, BorderLayout.WEST);
 
        JPanel accRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        accRow.setBackground(BG_PAGE);
        JLabel accLbl = new JLabel("Account:");
        accLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        accLbl.setForeground(TEXT_MUTED);
        accountSelector = new JComboBox<>();
        accountSelector.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        accountSelector.setPreferredSize(new Dimension(230, 30));
        accountSelector.addActionListener(e -> onAccountSelected());
        accRow.add(accLbl);
        accRow.add(accountSelector);
        topBar.add(accRow, BorderLayout.EAST);
        main.add(topBar);
 
        main.add(Box.createVerticalStrut(20));
 
        // ── Balance Card ─────────────────────────────────────────────
        JPanel balCard = new JPanel(new BorderLayout(0, 6));
        balCard.setBackground(ACCENT);
        balCard.setBorder(new EmptyBorder(22, 26, 22, 26));
        balCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        balCard.setAlignmentX(LEFT_ALIGNMENT);
 
        JLabel balTitle = new JLabel("Available Balance");
        balTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        balTitle.setForeground(new Color(186, 214, 255));
 
        balanceLabel = new JLabel("₹0.00");
        balanceLabel.setFont(new Font("Georgia", Font.BOLD, 38));
        balanceLabel.setForeground(Color.WHITE);
 
        accountInfoLabel = new JLabel("Select an account to view balance");
        accountInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        accountInfoLabel.setForeground(new Color(186, 214, 255));
 
        balCard.add(balTitle,       BorderLayout.NORTH);
        balCard.add(balanceLabel,   BorderLayout.CENTER);
        balCard.add(accountInfoLabel, BorderLayout.SOUTH);
        main.add(balCard);
 
        main.add(Box.createVerticalStrut(16));
 
        // ── Quick Actions ────────────────────────────────────────────
        JPanel actions = new JPanel(new GridLayout(1, 4, 12, 0));
        actions.setBackground(BG_PAGE);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        actions.setAlignmentX(LEFT_ALIGNMENT);
        actions.add(makeActionBtn("📥  Deposit",   ACCENT_GREEN,  this::openDepositDialog));
        actions.add(makeActionBtn("📤  Withdraw",  ACCENT_RED,    this::openWithdrawDialog));
        actions.add(makeActionBtn("🔄  Transfer",  ACCENT,        this::openTransferDialog));
        actions.add(makeActionBtn("🔃  Refresh",   ACCENT_AMBER,  this::refreshAll));
        main.add(actions);
 
        main.add(Box.createVerticalStrut(22));
 
        // ── Transaction History ──────────────────────────────────────
        JPanel histHeader = new JPanel(new BorderLayout());
        histHeader.setBackground(BG_PAGE);
        histHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        histHeader.setAlignmentX(LEFT_ALIGNMENT);
 
        JLabel histTitle = new JLabel("Transaction History");
        histTitle.setFont(new Font("Georgia", Font.BOLD, 17));
        histTitle.setForeground(TEXT_PRIMARY);
        histHeader.add(histTitle, BorderLayout.WEST);
 
        JLabel histSub = new JLabel("Last 50 transactions");
        histSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        histSub.setForeground(TEXT_MUTED);
        histHeader.add(histSub, BorderLayout.EAST);
        main.add(histHeader);
 
        main.add(Box.createVerticalStrut(10));
 
        String[] cols = {"Date & Time", "Type", "Amount (₹)", "Balance After (₹)", "Description", "Reference"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        transactionTable = new JTable(tableModel);
        styleTable(transactionTable);
 
        JScrollPane tableScroll = new JScrollPane(transactionTable);
        tableScroll.setBackground(BG_CARD);
        tableScroll.getViewport().setBackground(BG_CARD);
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        tableScroll.setAlignmentX(LEFT_ALIGNMENT);
        main.add(tableScroll);
 
        JScrollPane outerScroll = new JScrollPane(main);
        outerScroll.setBackground(BG_PAGE);
        outerScroll.getViewport().setBackground(BG_PAGE);
        outerScroll.setBorder(null);
        return outerScroll;
    }
 
    // ── Dialogs ────────────────────────────────────────────────────
 
    private void openCreateAccountDialog() {
        String[] types = {"SAVINGS", "CURRENT"};
        String choice = (String) JOptionPane.showInputDialog(this, "Select account type:",
                "Create New Account", JOptionPane.PLAIN_MESSAGE, null, types, types[0]);
        if (choice == null) return;
        new Thread(() -> {
            try {
                Account acc = accountDAO.createAccount(user.getUserId(), Account.AccountType.valueOf(choice));
                SwingUtilities.invokeLater(() -> {
                    if (acc != null) { showSuccess("Account created!\nNumber: " + acc.getAccountNumber()); loadUserAccounts(); }
                    else showError("Failed to create account.");
                });
            } catch (SQLException ex) { SwingUtilities.invokeLater(() -> showError("DB Error: " + ex.getMessage())); }
        }).start();
    }
 
    private void openDepositDialog() {
        if (selectedAccount == null) { showError("Select an account first."); return; }
        String amtStr = JOptionPane.showInputDialog(this, "Enter deposit amount (₹):", "Deposit", JOptionPane.PLAIN_MESSAGE);
        if (amtStr == null || amtStr.trim().isEmpty()) return;
        try {
            BigDecimal amount = new BigDecimal(amtStr.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) { showError("Amount must be positive."); return; }
            new Thread(() -> {
                try {
                    accountDAO.deposit(selectedAccount.getAccountId(), amount, "Cash Deposit");
                    SwingUtilities.invokeLater(() -> { showSuccess("₹" + amount + " deposited successfully!"); refreshAll(); });
                } catch (SQLException ex) { SwingUtilities.invokeLater(() -> showError("Error: " + ex.getMessage())); }
            }).start();
        } catch (NumberFormatException ex) { showError("Invalid amount entered."); }
    }
 
    private void openWithdrawDialog() {
        if (selectedAccount == null) { showError("Select an account first."); return; }
        String amtStr = JOptionPane.showInputDialog(this, "Enter withdrawal amount (₹):", "Withdraw", JOptionPane.PLAIN_MESSAGE);
        if (amtStr == null || amtStr.trim().isEmpty()) return;
        try {
            BigDecimal amount = new BigDecimal(amtStr.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) { showError("Amount must be positive."); return; }
            new Thread(() -> {
                try {
                    accountDAO.withdraw(selectedAccount.getAccountId(), amount, "Cash Withdrawal");
                    SwingUtilities.invokeLater(() -> { showSuccess("₹" + amount + " withdrawn successfully!"); refreshAll(); });
                } catch (IllegalArgumentException ex) { SwingUtilities.invokeLater(() -> showError(ex.getMessage())); }
                catch (SQLException ex) { SwingUtilities.invokeLater(() -> showError("Error: " + ex.getMessage())); }
            }).start();
        } catch (NumberFormatException ex) { showError("Invalid amount entered."); }
    }
 
    private void openTransferDialog() {
        if (selectedAccount == null) { showError("Select a source account first."); return; }
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 8));
        JTextField toAccField = new JTextField();
        JTextField amtField   = new JTextField();
        JTextField descField  = new JTextField("Fund Transfer");
        panel.add(new JLabel("Destination Account Number:"));
        panel.add(toAccField);
        panel.add(new JLabel("Amount (₹):"));
        panel.add(amtField);
        panel.add(new JLabel("Description:"));
        panel.add(descField);
 
        int result = JOptionPane.showConfirmDialog(this, panel, "Fund Transfer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
 
        String toAcc = toAccField.getText().trim();
        String desc  = descField.getText().trim();
        if (toAcc.isEmpty()) { showError("Enter destination account number."); return; }
        if (toAcc.equals(selectedAccount.getAccountNumber())) { showError("Cannot transfer to the same account."); return; }
        try {
            BigDecimal amount = new BigDecimal(amtField.getText().trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) { showError("Amount must be positive."); return; }
            new Thread(() -> {
                try {
                    accountDAO.transfer(selectedAccount.getAccountId(), toAcc, amount, desc);
                    SwingUtilities.invokeLater(() -> { showSuccess("₹" + amount + " transferred successfully!"); refreshAll(); });
                } catch (IllegalArgumentException ex) { SwingUtilities.invokeLater(() -> showError(ex.getMessage())); }
                catch (SQLException ex) { SwingUtilities.invokeLater(() -> showError("Error: " + ex.getMessage())); }
            }).start();
        } catch (NumberFormatException ex) { showError("Invalid amount entered."); }
    }
 
    // ── Data ───────────────────────────────────────────────────────
 
    private void loadUserAccounts() {
        new Thread(() -> {
            try {
                List<Account> accounts = accountDAO.getAccountsByUser(user.getUserId());
                SwingUtilities.invokeLater(() -> {
                    accountSelector.removeAllItems();
                    for (Account a : accounts) accountSelector.addItem(a);
                    if (!accounts.isEmpty()) { accountSelector.setSelectedIndex(0); onAccountSelected(); }
                });
            } catch (SQLException ex) { SwingUtilities.invokeLater(() -> showError("Error loading accounts: " + ex.getMessage())); }
        }).start();
    }
 
    private void onAccountSelected() {
        selectedAccount = (Account) accountSelector.getSelectedItem();
        if (selectedAccount == null) return;
        new Thread(() -> {
            try {
                Account fresh = accountDAO.findByAccountNumber(selectedAccount.getAccountNumber());
                if (fresh != null) { selectedAccount = fresh; SwingUtilities.invokeLater(this::updateBalanceCard); }
            } catch (SQLException ex) { ex.printStackTrace(); }
        }).start();
        refreshTransactionHistory();
    }
 
    private void updateBalanceCard() {
        balanceLabel.setText("₹" + String.format("%,.2f", selectedAccount.getBalance()));
        accountInfoLabel.setText(selectedAccount.getAccountNumber() + "   •   " + selectedAccount.getAccountType() + " Account");
    }
 
    private void refreshTransactionHistory() {
        if (selectedAccount == null) return;
        new Thread(() -> {
            try {
                List<Transaction> txns = accountDAO.getTransactionHistory(selectedAccount.getAccountId(), 50);
                SwingUtilities.invokeLater(() -> populateTable(txns));
            } catch (SQLException ex) { SwingUtilities.invokeLater(() -> showError("Error loading history: " + ex.getMessage())); }
        }).start();
    }
 
    private void populateTable(List<Transaction> txns) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy  HH:mm");
        for (Transaction t : txns) {
            boolean credit = t.getTransactionType() == Transaction.TransactionType.DEPOSIT
                          || t.getTransactionType() == Transaction.TransactionType.TRANSFER_IN;
            tableModel.addRow(new Object[]{
                    sdf.format(t.getTransactionDate()),
                    t.getTransactionType().name().replace("_", " "),
                    (credit ? "+" : "-") + String.format("%,.2f", t.getAmount()),
                    String.format("%,.2f", t.getBalanceAfter()),
                    t.getDescription() != null ? t.getDescription() : "",
                    t.getReferenceAccount() != null ? t.getReferenceAccount() : ""
            });
        }
    }
 
    private void refreshAll() { loadUserAccounts(); }
 
    private void logout() {
        int c = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                "Logout", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) { dispose(); new LoginFrame().setVisible(true); }
    }
 
    // ── UI Helpers ─────────────────────────────────────────────────
 
    private void styleTable(JTable table) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_COLOR);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(32);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
 
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
 
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? BG_CARD : BG_TABLE_ROW);
                    String type = (String) t.getValueAt(row, 1);
                    if (col == 2) {
                        boolean credit = type.contains("DEPOSIT") || type.contains("TRANSFER IN");
                        setForeground(credit ? ACCENT_GREEN : ACCENT_RED);
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else {
                        setForeground(TEXT_PRIMARY);
                        setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    }
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }
 
    private JButton makeActionBtn(String text, Color bg, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }
 
    private JButton makeSidebarBtn(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(BG_SIDEBAR);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setBorder(new EmptyBorder(0, 8, 0, 8));
        btn.addActionListener(e -> action.run());
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(SIDEBAR_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(BG_SIDEBAR); }
        });
        return btn;
    }
 
    private void showError(String msg)   { JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE); }
    private void showSuccess(String msg) { JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE); }

}
