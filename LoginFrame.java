package railway.gui;
import railway.db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Railway Management System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        UIUtils.styleFrame(this); // Theme frame background

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        UIUtils.stylePanel(panel);

        JLabel title = new JLabel("Railway Login", SwingConstants.LEFT);
        UIUtils.styleTitle(title);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel userLabel = new JLabel("Username:");
        UIUtils.styleLabel(userLabel);
        panel.add(userLabel);
        usernameField = new JTextField(15);
        UIUtils.styleTextField(usernameField);
        panel.add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        UIUtils.styleLabel(passLabel);
        panel.add(passLabel);
        passwordField = new JPasswordField(15);
        UIUtils.styleTextField(passwordField);
        panel.add(passwordField);

        loginButton = new JButton("Login");
        UIUtils.styleButton(loginButton);
        loginButton.addActionListener(this);

        JButton registerButton = new JButton("Register");
        UIUtils.styleButton(registerButton);
        registerButton.addActionListener(e -> {
            dispose();
            new RegisterFrame();
        });

        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
        UIUtils.stylePanel(buttonRow);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonRow.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonRow.add(loginButton);
        buttonRow.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonRow.add(registerButton);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonRow);
        add(panel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "✅ Login successful!");
                    dispose();
                    if (username.equalsIgnoreCase("admin")) {
                        new AdminDashboardFrame(username).setVisible(true);
                    } else {
                        new DashboardFrame(username);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Invalid credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
