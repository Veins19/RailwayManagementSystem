/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package railway.gui;
import railway.db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
/**
 *
 * @author tharu
 */
public class LoginFrame extends JFrame implements ActionListener{
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Railway Management System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame

        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30)); // dark background
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // 🛤️ Emoji Header
        JLabel title = new JLabel("Railway Login", SwingConstants.LEFT);
        title.setForeground(new Color(0x00FFD1));
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 🧑 Username
        JLabel userLabel = makeLabel("Username:");
        panel.add(userLabel);
        usernameField = makeTextField();
        panel.add(usernameField);

        // 🔒 Password
        JLabel passLabel = makeLabel("Password:");
        panel.add(passLabel);
        passwordField = new JPasswordField(15);
        styleField(passwordField);
        panel.add(passwordField);

        // 🔘 Login Button
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0x00FFD1)); // vibrant color
        loginButton.setForeground(Color.BLACK);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setPreferredSize(new Dimension(120, 35));
        loginButton.addActionListener(this);
        
        
        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(0x00FFD1));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerButton.setFocusPainted(false);
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.setPreferredSize(new Dimension(120, 35));
        registerButton.addActionListener(e -> {
            dispose();
            new RegisterFrame();
        });
        
        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonRow.setBackground(panel.getBackground());
        buttonRow.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonRow.add(loginButton);
        buttonRow.add(Box.createRigidArea(new Dimension(20, 0))); // spacing
        buttonRow.add(registerButton);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonRow);
       

        add(panel);
        setVisible(true);

    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.LIGHT_GRAY);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField makeTextField() {
        JTextField tf = new JTextField(15);
        styleField(tf);
        return tf;
    }

    private void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(220, 30)); // Smaller width
        field.setBackground(new Color(45, 45, 45));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
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
                    dispose(); // close login window

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