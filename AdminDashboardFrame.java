/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package railway.gui;
import railway.db.BookingReportGenerator;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

/**
 *
 * @author tharu
 */
public class AdminDashboardFrame extends JFrame {
    public AdminDashboardFrame(String username) {
        setTitle("Admin Dashboard");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Dark theme base
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        JLabel welcomeLabel = new JLabel("Welcome, Admin", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(0x00FFD1));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        buttonPanel.setBackground(new Color(30, 30, 30));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        String[] actions = {
            "Add Train", "View All Trains",
            "View All Bookings", "View Users",
            "Export Report", "Logout" //, "Edit/Delete Train"
        };

        for (String action : actions) {
            JButton btn = new JButton(action);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btn.setBackground(new Color(0x00FFD1));
            btn.setForeground(Color.BLACK);
            btn.setPreferredSize(new Dimension(200, 60));
            btn.addActionListener(e -> handleAction(action));
            buttonPanel.add(btn);
        }

        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }
    public static void deleteTrain(int trainId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/railway_db", "root", "Tharun@123")) {

            // 1. Update affected bookings to status = 'Deleted'
            String updateBookingsSQL = "UPDATE bookings SET status = 'Deleted' WHERE train_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateBookingsSQL)) {
                updateStmt.setInt(1, trainId);
                updateStmt.executeUpdate();
            }

            // 2. Now delete the train
            String deleteTrainSQL = "DELETE FROM trains WHERE train_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteTrainSQL)) {
                deleteStmt.setInt(1, trainId);
                deleteStmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(null, "Train deleted successfully, bookings updated as 'Deleted'");

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