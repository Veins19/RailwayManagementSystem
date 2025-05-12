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
public class CancelBookingFrame extends JFrame {
    private JTable bookingTable;
    private DashboardFrame parent;

    public CancelBookingFrame(String username, DashboardFrame parent) {
        this.parent=parent;
        setTitle("Cancel Booking");
        setSize(600, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columns = {"Booking ID", "Train Name", "Seats Booked", "Booking Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        bookingTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(bookingTable);

        JButton cancelButton = new JButton("Cancel Selected Booking");
        cancelButton.setBackground(new Color(0xFFB2B2));
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.addActionListener(e -> cancelSelectedBooking(username));

        add(scrollPane, BorderLayout.CENTER);
        add(cancelButton, BorderLayout.SOUTH);

        loadBookings(username, model);
        setVisible(true);
    }

    private void loadBookings(String username, DefaultTableModel model) {
        try (Connection conn = DBConnection.getConnection()) {
            String query =
                "SELECT b.booking_id, t.train_name, b.seats_booked, b.booking_date " +
                "FROM bookings b " +
                "JOIN trains t ON b.train_id = t.train_id " +
                "JOIN users u ON b.user_id = u.id " +
                "WHERE u.username = ? AND b.cancelled_on IS NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("booking_id"),
                    rs.getString("train_name"),
                    rs.getInt("seats_booked"),
                    rs.getDate("booking_date")
                };
                model.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading booking data.", "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void cancelSelectedBooking(String username) {
    int selectedRow = bookingTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this booking?", "Confirm", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) return;

    int bookingId = (int) bookingTable.getValueAt(selectedRow, 0);
    int seatsToRestore = (int) bookingTable.getValueAt(selectedRow, 2);

    try (Connection conn = DBConnection.getConnection()) {
        // Get train_id before update
        int trainId = -1;
        PreparedStatement getTrainId = conn.prepareStatement("SELECT train_id FROM bookings WHERE booking_id = ?");
        getTrainId.setInt(1, bookingId);
        ResultSet rs = getTrainId.executeQuery();
        if (rs.next()) {
            trainId = rs.getInt("train_id");
        }

        // Mark booking as cancelled (set cancelled_on to today)
        PreparedStatement cancelBooking = conn.prepareStatement("UPDATE bookings SET cancelled_on = CURRENT_DATE, status = 'Cancelled' WHERE booking_id = ?");
        cancelBooking.setInt(1, bookingId);
        int rows = cancelBooking.executeUpdate();

        // Restore seat count
        PreparedStatement updateTrain = conn.prepareStatement("UPDATE trains SET total_seats = total_seats + ? WHERE train_id = ?");
        updateTrain.setInt(1, seatsToRestore);
        updateTrain.setInt(2, trainId);
        updateTrain.executeUpdate();

        if (rows > 0) {
            JOptionPane.showMessageDialog(this, "Booking cancelled successfully.");
            ((DefaultTableModel) bookingTable.getModel()).removeRow(selectedRow);
            parent.reloadTrainTable();
        }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cancelling booking.", "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
