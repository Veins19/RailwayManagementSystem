package railway.gui;

import railway.db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterFrame extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, backButton;

    public RegisterFrame() {
        setTitle("Register - Railway System");
        setSize(400, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        UIUtils.stylePanel(panel);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel titleLabel = new JLabel("Create New Account");
        UIUtils.styleTitle(titleLabel);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(makeLabel("Username:"));
        usernameField = makeField();
        panel.add(usernameField);

        panel.add(makeLabel("Password:"));
        passwordField = makePasswordField();
        panel.add(passwordField);

        panel.add(makeLabel("Confirm Password:"));
        confirmPasswordField = makePasswordField();
        panel.add(confirmPasswordField);

        // Buttons
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");

        UIUtils.styleButton(registerButton);
        UIUtils.styleButton(backButton);

        registerButton.setPreferredSize(new Dimension(120, 35));
        backButton.setPreferredSize(new Dimension(120, 35));

        registerButton.addActionListener(this);
        backButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        UIUtils.stylePanel(buttonPanel);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.add(registerButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(backButton);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);

        add(panel);
        setVisible(true);
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        UIUtils.styleLabel(label);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField makeField() {
        JTextField tf = new JTextField(15);
        tf.setMaximumSize(new Dimension(220, 30));
        UIUtils.styleTextField(tf);
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }

    private JPasswordField makePasswordField() {
        JPasswordField pf = new JPasswordField(15);
        pf.setMaximumSize(new Dimension(220, 30));
        UIUtils.styleTextField(pf);
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return pf;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = String.valueOf(passwordField.getPassword()).trim();
        String confirmPassword = String.valueOf(confirmPasswordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Password Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String check = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(check);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Username already exists.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'passenger')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Registration successful! You can now log in.");
                dispose();
                new LoginFrame();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during registration.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
