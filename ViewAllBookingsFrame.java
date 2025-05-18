package railway.gui;

import railway.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

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
        UIUtils.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);

        JLabel title = new JLabel("All Bookings", SwingConstants.CENTER);
        UIUtils.styleTitle(title);

        JPanel panel = new JPanel(new BorderLayout());
        UIUtils.stylePanel(panel);
        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        add(panel);
        loadBookings(model);
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
