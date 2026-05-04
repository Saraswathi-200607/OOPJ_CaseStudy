package com.banking.ui;

import com.banking.dao.UserDAO;
import com.banking.model.User;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;


public class LoginFrame extends JFrame {
    private static final Color BG_PAGE       = new Color(243, 246, 250);
    private static final Color BG_HEADER     = new Color(25, 100, 195);
    private static final Color ACCENT        = new Color(25, 100, 195);
    private static final Color TEXT_PRIMARY  = new Color(30,  40,  60);
    private static final Color TEXT_MUTED    = new Color(100, 116, 139);
    private static final Color FIELD_BG      = new Color(248, 250, 252);
    private static final Color FIELD_BORDER  = new Color(203, 213, 225);
    private static final Color ERROR_COLOR   = new Color(200,  40,  40);
    private static final Color SUCCESS_COLOR = new Color(22, 163,  74);
    private static final Color DIVIDER       = new Color(226, 232, 240);
 
    private final UserDAO userDAO = new UserDAO();
 
    private JTextField     loginUsernameField;
    private JPasswordField loginPasswordField;
    private JLabel         loginStatusLabel;
 
    private JTextField     regFullNameField, regEmailField, regUsernameField;
    private JPasswordField regPasswordField, regConfirmPasswordField;
    private JLabel         regStatusLabel;
 
    private JTabbedPane tabbedPane;
 
    public LoginFrame() {
        setTitle("SecureBank — Online Banking");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(460, 595);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG_PAGE);
        setLayout(new BorderLayout());
 
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(),   BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }
 
    private JPanel buildHeader() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_HEADER);
 
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 18));
        top.setBackground(BG_HEADER);
        JLabel icon  = new JLabel("🏦");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        JLabel title = new JLabel("  SecureBank");
        title.setFont(new Font("Georgia", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        top.add(icon);
        top.add(title);
 
        JLabel tag = new JLabel("Your trusted digital banking partner", SwingConstants.CENTER);
        tag.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tag.setForeground(new Color(186, 214, 255));
        tag.setBorder(new EmptyBorder(0, 0, 14, 0));
 
        wrapper.add(top, BorderLayout.CENTER);
        wrapper.add(tag, BorderLayout.SOUTH);
        return wrapper;
    }
 
    private JTabbedPane buildTabs() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BG_PAGE);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBorder(new EmptyBorder(10, 20, 0, 20));
        tabbedPane.addTab("   Login   ",    buildLoginPanel());
        tabbedPane.addTab("   Register   ", buildRegisterPanel());
        return tabbedPane;
    }
 
    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_PAGE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));
 
        panel.add(Box.createVerticalStrut(8));
        panel.add(makeLabel("Username"));
        panel.add(Box.createVerticalStrut(5));
        loginUsernameField = makeTextField();
        panel.add(loginUsernameField);
 
        panel.add(Box.createVerticalStrut(14));
        panel.add(makeLabel("Password"));
        panel.add(Box.createVerticalStrut(5));
        loginPasswordField = makePasswordField();
        panel.add(loginPasswordField);
 
        panel.add(Box.createVerticalStrut(24));
        JButton loginBtn = makeButton("Login to My Account", ACCENT, Color.WHITE);
        loginBtn.addActionListener(this::handleLogin);
        panel.add(loginBtn);
 
        panel.add(Box.createVerticalStrut(12));
        loginStatusLabel = makeStatusLabel();
        panel.add(loginStatusLabel);
        return panel;
    }
 
    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_PAGE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(14, 40, 14, 40));
 
        panel.add(makeLabel("Full Name"));
        panel.add(Box.createVerticalStrut(5));
        regFullNameField = makeTextField();
        panel.add(regFullNameField);
 
        panel.add(Box.createVerticalStrut(10));
        panel.add(makeLabel("Email Address"));
        panel.add(Box.createVerticalStrut(5));
        regEmailField = makeTextField();
        panel.add(regEmailField);
 
        panel.add(Box.createVerticalStrut(10));
        panel.add(makeLabel("Username"));
        panel.add(Box.createVerticalStrut(5));
        regUsernameField = makeTextField();
        panel.add(regUsernameField);
 
        panel.add(Box.createVerticalStrut(10));
        panel.add(makeLabel("Password"));
        panel.add(Box.createVerticalStrut(5));
        regPasswordField = makePasswordField();
        panel.add(regPasswordField);
 
        panel.add(Box.createVerticalStrut(10));
        panel.add(makeLabel("Confirm Password"));
        panel.add(Box.createVerticalStrut(5));
        regConfirmPasswordField = makePasswordField();
        panel.add(regConfirmPasswordField);
 
        panel.add(Box.createVerticalStrut(18));
        JButton regBtn = makeButton("Create Account", new Color(22, 163, 74), Color.WHITE);
        regBtn.addActionListener(this::handleRegister);
        panel.add(regBtn);
 
        panel.add(Box.createVerticalStrut(10));
        regStatusLabel = makeStatusLabel();
        panel.add(regStatusLabel);
        return panel;
    }
 
    private JPanel buildFooter() {
        JPanel footer = new JPanel();
        footer.setBackground(BG_PAGE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, DIVIDER),
                new EmptyBorder(8, 0, 10, 0)));
        JLabel lbl = new JLabel("© 2025 SecureBank  •  All transactions are secured & encrypted");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_MUTED);
        footer.add(lbl);
        return footer;
    }
 
    private void handleLogin(ActionEvent e) {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            setStatus(loginStatusLabel, "⚠  Please fill in all fields.", ERROR_COLOR); return;
        }
        setStatus(loginStatusLabel, "Authenticating...", TEXT_MUTED);
        new Thread(() -> {
            try {
                User user = userDAO.login(username, password);
                SwingUtilities.invokeLater(() -> {
                    if (user != null) {
                        setStatus(loginStatusLabel, "✔  Login successful!", SUCCESS_COLOR);
                        Timer t = new Timer(600, ev -> { dispose(); new DashboardFrame(user).setVisible(true); });
                        t.setRepeats(false); t.start();
                    } else {
                        setStatus(loginStatusLabel, "✘  Invalid username or password.", ERROR_COLOR);
                    }
                });
            } catch (SQLException ex) {
                SwingUtilities.invokeLater(() -> setStatus(loginStatusLabel, "DB error: " + ex.getMessage(), ERROR_COLOR));
            }
        }).start();
    }
 
    private void handleRegister(ActionEvent e) {
        String fullName = regFullNameField.getText().trim();
        String email    = regEmailField.getText().trim();
        String username = regUsernameField.getText().trim();
        String password = new String(regPasswordField.getPassword());
        String confirm  = new String(regConfirmPasswordField.getPassword());
 
        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            setStatus(regStatusLabel, "⚠  All fields are required.", ERROR_COLOR); return;
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            setStatus(regStatusLabel, "⚠  Enter a valid email address.", ERROR_COLOR); return;
        }
        if (password.length() < 6) {
            setStatus(regStatusLabel, "⚠  Password must be at least 6 characters.", ERROR_COLOR); return;
        }
        if (!password.equals(confirm)) {
            setStatus(regStatusLabel, "⚠  Passwords do not match.", ERROR_COLOR); return;
        }
        setStatus(regStatusLabel, "Creating account...", TEXT_MUTED);
        new Thread(() -> {
            try {
                if (userDAO.usernameExists(username)) {
                    SwingUtilities.invokeLater(() -> setStatus(regStatusLabel, "✘  Username already taken.", ERROR_COLOR)); return;
                }
                if (userDAO.emailExists(email)) {
                    SwingUtilities.invokeLater(() -> setStatus(regStatusLabel, "✘  Email already registered.", ERROR_COLOR)); return;
                }
                int userId = userDAO.registerUser(fullName, email, username, password);
                SwingUtilities.invokeLater(() -> {
                    if (userId > 0) {
                        setStatus(regStatusLabel, "✔  Account created! Please login.", SUCCESS_COLOR);
                        regFullNameField.setText(""); regEmailField.setText("");
                        regUsernameField.setText(""); regPasswordField.setText(""); regConfirmPasswordField.setText("");
                        tabbedPane.setSelectedIndex(0);
                    } else {
                        setStatus(regStatusLabel, "✘  Registration failed.", ERROR_COLOR);
                    }
                });
            } catch (SQLException ex) {
                SwingUtilities.invokeLater(() -> setStatus(regStatusLabel, "DB error: " + ex.getMessage(), ERROR_COLOR));
            }
        }).start();
    }
 
    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
 
    private JTextField makeTextField() { JTextField f = new JTextField(); styleField(f); return f; }
    private JPasswordField makePasswordField() { JPasswordField f = new JPasswordField(); styleField(f); return f; }
 
    private void styleField(JTextField field) {
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT, 2),
                        BorderFactory.createEmptyBorder(7, 9, 7, 9)));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FIELD_BORDER, 1),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            }
        });
    }
 
    private JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }
 
    private JLabel makeStatusLabel() {
        JLabel lbl = new JLabel(" ");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
 
    private void setStatus(JLabel label, String text, Color color) {
        label.setText(text); label.setForeground(color);
    }


}
