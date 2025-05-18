package railway.gui;

import railway.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ReviewTrainFrame extends JFrame {
    private JTable table;
    private JTextArea reviewArea;
    private JComboBox<Integer> ratingBox;
    private DefaultTableModel model;

    public ReviewTrainFrame(String username) {
        setTitle("Write Review");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        UIUtils.stylePanel(mainPanel);

        // Title
        JLabel title = new JLabel("Leave a Review for Your Journey", SwingConstants.CENTER);
        UIUtils.styleTitle(title);
        mainPanel.add(title, BorderLayout.NORTH);

        // Table of eligible trains
        model = new DefaultTableModel(new String[]{"Booking ID", "Train Name", "Source", "Destination", "Date"}, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        UIUtils.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // Review form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        UIUtils.stylePanel(formPanel);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel reviewLabel = new JLabel("Your Review:");
        UIUtils.styleLabel(reviewLabel);
        reviewArea = new JTextArea(4, 40);
        reviewArea.setWrapStyleWord(true);
        reviewArea.setLineWrap(true);
        reviewArea.setFont(UIUtils.DEFAULT_FONT);
        reviewArea.setBackground(UIUtils.FIELD_BG);
        reviewArea.setForeground(Color.WHITE);
        reviewArea.setCaretColor(Color.WHITE);
        reviewArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane reviewScroll = new JScrollPane(reviewArea);

        JLabel ratingLabel = new JLabel("Rating (1 to 5):");
        UIUtils.styleLabel(ratingLabel);
        ratingBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        ratingBox.setFont(UIUtils.DEFAULT_FONT);
        ratingBox.setBackground(UIUtils.FIELD_BG);
        ratingBox.setForeground(Color.WHITE);

        JButton submitBtn = new JButton("Submit Review");
        UIUtils.styleButton(submitBtn);
        submitBtn.addActionListener(e -> submitReview(username));

        formPanel.add(reviewLabel);
        formPanel.add(reviewScroll);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(ratingLabel);
        formPanel.add(ratingBox);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(submitBtn);

        mainPanel.add(formPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadEligibleBookings(username);
        setVisible(true);
    }

    private void loadEligibleBookings(String username) {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
                SELECT b.booking_id, b.train_name, b.source, b.destination, b.booking_date
                FROM bookings b
                JOIN users u ON b.user_id = u.id
                WHERE u.username = ? AND b.status = 'Confirmed'
                AND NOT EXISTS (
                    SELECT 1 FROM reviews r WHERE r.booking_id = b.booking_id
                )
                """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("train_name"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getDate("booking_date")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bookings.", "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitReview(String username) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to review.");
            return;
        }

        int bookingId = (int) model.getValueAt(selectedRow, 0);
        String reviewText = reviewArea.getText().trim();
        int rating = (int) ratingBox.getSelectedItem();

        if (reviewText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a review.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String insert = "INSERT INTO reviews (booking_id, review_text, rating) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insert);
            stmt.setInt(1, bookingId);
            stmt.setString(2, reviewText);
            stmt.setInt(3, rating);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Review submitted!");
                reviewArea.setText("");
                loadEligibleBookings(username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error submitting review.", "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
