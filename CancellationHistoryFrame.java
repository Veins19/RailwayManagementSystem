package railway.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import railway.db.DBConnection;
import java.awt.*;
import java.sql.*;

public class CancellationHistoryFrame extends JFrame {

    public CancellationHistoryFrame(String username) {
        setTitle("Cancellation History - " + username);
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Set up the main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        UIUtils.stylePanel(mainPanel);

        // Heading
        JLabel heading = new JLabel("Your Cancellation History", SwingConstants.CENTER);
        UIUtils.styleTitle(heading);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        mainPanel.add(heading, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"Booking ID", "Train Name", "Source", "Destination", "Seats Canceled", "Cancellation Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        UIUtils.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(UIUtils.FIELD_BG);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        loadCancellationHistory(username, model);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private void loadCancellationHistory(String username, DefaultTableModel model) {
        try (Connection conn = DBConnection.getConnection()) {
            // Get user ID
            PreparedStatement getUser = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            getUser.setString(1, username);
            ResultSet rsUser = getUser.executeQuery();

            int userId = -1;
            if (rsUser.next()) {
                userId = rsUser.getInt("id");
            }

            // Fetch canceled bookings
            String sql = "SELECT b.booking_id, t.train_name, t.source, t.destination, b.seats_booked, b.cancelled_on " +
                         "FROM bookings b " +
                         "JOIN trains t ON b.train_id = t.train_id " +
                         "WHERE b.user_id = ? AND b.cancelled_on IS NOT NULL";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("train_name"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getInt("seats_booked"),
                    rs.getDate("cancelled_on")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading cancellation history.", "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
