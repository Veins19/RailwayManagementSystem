package railway.gui;

import railway.db.BookingReportGenerator;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminDashboardFrame extends JFrame {

    public AdminDashboardFrame(String username) {
        setTitle("Admin Dashboard");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        UIUtils.stylePanel(mainPanel);

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, Admin", SwingConstants.CENTER);
        UIUtils.styleTitle(welcomeLabel);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Grid layout for action buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        UIUtils.stylePanel(buttonPanel);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        String[] actions = {
            "Add Train", "View All Trains",
            "View All Bookings", "View Users", "View Reviews",
            "Export Report", "Logout"
        };

        for (String action : actions) {
            JButton btn = new JButton(action);
            UIUtils.styleButton(btn);
            btn.setPreferredSize(new Dimension(200, 60));
            btn.addActionListener(e -> handleAction(action));
            buttonPanel.add(btn);
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    public static void deleteTrain(int trainId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/railway_db", "root", "Tharun@123")) {

            // 1. Update bookings for the train
            String updateBookingsSQL = "UPDATE bookings SET status = 'Deleted' WHERE train_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateBookingsSQL)) {
                updateStmt.setInt(1, trainId);
                updateStmt.executeUpdate();
            }

            // 2. Delete the train itself
            String deleteTrainSQL = "DELETE FROM trains WHERE train_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteTrainSQL)) {
                deleteStmt.setInt(1, trainId);
                deleteStmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(null, "Train deleted successfully. Related bookings marked as 'Deleted'.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting train: " + e.getMessage());
        }
    }

    private void handleAction(String action) {
        switch (action) {
            case "Add Train":
                new AddTrainFrame().setVisible(true);
                break;
            case "View All Trains":
                new ViewAllTrainsFrame().setVisible(true);
                break;
            case "View All Bookings":
                new ViewAllBookingsFrame().setVisible(true);
                break;
            case "View Users":
                new ViewAllUsersFrame().setVisible(true);
                break;
            case "View Reviews":
                new ViewReviewsFrame().setVisible(true);
                break;
            case "Export Report":
                BookingReportGenerator.generatePDFReport("Booking_Report.pdf");
                break;
            case "Logout":
                dispose();
                new LoginFrame();
                break;
        }
    }
}
