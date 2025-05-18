package railway.gui;

import railway.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewAllUsersFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public ViewAllUsersFrame() {
        setTitle("All Users");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"User ID", "Username", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        UIUtils.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);

        JButton deleteBtn = new JButton("Delete Selected User");
        UIUtils.styleButton(deleteBtn);
        deleteBtn.setBackground(new Color(0xFF8888));  // slightly reddish for delete

        deleteBtn.addActionListener(e -> deleteUser());

        JPanel bottomPanel = new JPanel();
        UIUtils.stylePanel(bottomPanel);
        bottomPanel.add(deleteBtn);

        JLabel title = new JLabel("All Registered Users", SwingConstants.CENTER);
        UIUtils.styleTitle(title);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel panel = new JPanel(new BorderLayout());
        UIUtils.stylePanel(panel);
        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);
        loadUsers();
        UIUtils.styleFrame(this);
        setVisible(true);
    }

    private void loadUsers() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, username, role FROM users")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }

        int userId = (int) model.getValueAt(row, 0);
        String username = (String) model.getValueAt(row, 1);
        String role = (String) model.getValueAt(row, 2);

        if ("admin".equalsIgnoreCase(role)) {
            JOptionPane.showMessageDialog(this, "❌ You cannot delete an admin account.", "Action Blocked", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user '" + username + "'?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {

            stmt.setInt(1, userId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ User deleted successfully.");
                model.removeRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage());
        }
    }
}
