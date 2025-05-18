package railway.gui;

import railway.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewReviewsFrame extends JFrame {
    public ViewReviewsFrame() {
        setTitle("All User Reviews");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        UIUtils.stylePanel(panel);

        JLabel title = new JLabel("Passenger Reviews", SwingConstants.CENTER);
        UIUtils.styleTitle(title);
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"Booking ID", "Train", "From", "To", "Rating", "Review"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        UIUtils.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
        loadReviews(model);
        setVisible(true);
    }

    private void loadReviews(DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
                SELECT r.booking_id, b.train_name, b.source, b.destination, r.rating, r.review_text
                FROM reviews r
                JOIN bookings b ON r.booking_id = b.booking_id
                ORDER BY r.booking_id DESC
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("train_name"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getInt("rating"),
                        rs.getString("review_text")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading reviews: " + e.getMessage());
        }
    }
}
