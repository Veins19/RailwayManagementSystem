package railway.gui;

import railway.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BookingHistoryFrame extends JFrame {
    public BookingHistoryFrame(String username) {
        setTitle("Your Booking History");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel setup
        JPanel panel = new JPanel(new BorderLayout());
        UIUtils.stylePanel(panel);

        // Title label
        JLabel titleLabel = new JLabel("Booking History", SwingConstants.CENTER);
        UIUtils.styleTitle(titleLabel);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"Booking ID", "Train Name", "From", "To", "Seats Booked", "Booking Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        UIUtils.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(UIUtils.FIELD_BG);
        panel.add(scrollPane, BorderLayout.CENTER);

        loadBookings(username, model);

        add(panel);
        setVisible(true);
    }

    private void loadBookings(String username, DefaultTableModel model) {
        try (Connection conn = DBConnection.getConnection()) {
            String query =
                "SELECT b.booking_id, b.train_name, b.source, b.destination, b.seats_booked, b.booking_date, " +
                "       b.status " +
                "FROM bookings b " +
                "JOIN users u ON b.user_id = u.id " +
                "WHERE u.username = ? " +
                "ORDER BY b.booking_date DESC";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String status = rs.getString("status");
                if (status == null) status = "Confirmed";
                model.addRow(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("train_name"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getInt("seats_booked"),
                    rs.getDate("booking_date"),
                    status
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading booking history.", "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
