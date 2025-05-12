/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package railway.gui;
import railway.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
/**
 *
 * @author tharu
 */
public class ViewAllBookingsFrame extends JFrame {
    public ViewAllBookingsFrame() {
        setTitle("All Bookings");
        setSize(950, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columns = {
            "Booking ID", "Username", "Train", "From", "To",
            "Seats", "Date", "Status"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make table read-only
            }
        };

        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        loadBookings(model);

        JLabel title = new JLabel("All Bookings", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0x00FFD1));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setBackground(new Color(50, 50, 50));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(70, 130, 180));

        add(panel);
        setVisible(true);
    }

    private void loadBookings(DefaultTableModel model) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = """
                SELECT b.booking_id, u.username, b.train_name, b.source, b.destination,
                       b.seats_booked, b.booking_date, b.status
                FROM bookings b
                JOIN users u ON b.user_id = u.id
                ORDER BY b.booking_date DESC
                """;

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("username"),
                    rs.getString("train_name"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getInt("seats_booked"),
                    rs.getDate("booking_date"),
                    rs.getString("status")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + e.getMessage());
        }
    }
}
