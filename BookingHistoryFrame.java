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
public class BookingHistoryFrame extends JFrame {
    public BookingHistoryFrame(String username) {
        setTitle("Your Booking History");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columns = {"Booking ID", "Train Name", "From", "To", "Seats Booked", "Booking Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        loadBookings(username, model);

        add(scrollPane);
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
                if (status == null) status = "Confirmed"; // fallback
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
