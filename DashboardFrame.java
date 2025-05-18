package railway.gui;

import railway.db.DBConnection;
import railway.db.PDFGenerator;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class DashboardFrame extends JFrame {
    private JTable trainTable;
    private JComboBox<String> sourceFilter, destinationFilter;
    private DefaultTableModel model;

    public DashboardFrame(String username) {
        setTitle("Dashboard - Welcome " + username);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Top title
        JLabel titleLabel = new JLabel("Available Trains");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0x00FF00));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        String[] columns = {"Train ID", "Train Name", "Source", "Destination", "Departure", "Arrival", "Date", "Seats"};
        model = new DefaultTableModel(columns, 0);
        trainTable = new JTable(model);
        trainTable.setRowSorter(new TableRowSorter<>(model));
        UIUtils.styleTable(trainTable);
        JScrollPane scrollPane = new JScrollPane(trainTable);
        UIUtils.setDarkBackground(scrollPane);

        // Filter combo boxes
        sourceFilter = new JComboBox<>();
        destinationFilter = new JComboBox<>();
        sourceFilter.addItem("All");
        destinationFilter.addItem("All");
        sourceFilter.addActionListener(e -> applyFilters());
        destinationFilter.addActionListener(e -> applyFilters());

        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(new Color(30, 30, 30));
        filterPanel.setForeground(Color.WHITE);
        filterPanel.add(UIUtils.makeDarkLabel("Source:"));
        filterPanel.add(sourceFilter);
        filterPanel.add(UIUtils.makeDarkLabel("Destination:"));
        filterPanel.add(destinationFilter);

        // Buttons
        JButton bookButton = new JButton("Book Ticket");
        JButton cancelButton = new JButton("Cancel Booking");
        JButton historyButton = new JButton("View Booking History");
        JButton cancelHistoryButton = new JButton("Cancellation History");
        JButton logoutButton = new JButton("Logout");
        JButton reviewButton = new JButton("Review Trains");


        UIUtils.styleButton(bookButton);
        UIUtils.styleButton(cancelButton);
        UIUtils.styleButton(historyButton);
        UIUtils.styleButton(cancelHistoryButton);
        UIUtils.styleButton(logoutButton);
        UIUtils.styleButton(reviewButton);
        
        reviewButton.addActionListener(e -> new ReviewTrainFrame(username));
        bookButton.addActionListener(e -> handleBooking(username));
        cancelButton.addActionListener(e -> new CancelBookingFrame(username, this));
        historyButton.addActionListener(e -> new BookingHistoryFrame(username));
        cancelHistoryButton.addActionListener(e -> new CancellationHistoryFrame(username));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        UIUtils.setDarkBackground(buttonPanel);
        buttonPanel.add(bookButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(cancelHistoryButton);
        buttonPanel.add(reviewButton);
        buttonPanel.add(logoutButton);

        // Layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        UIUtils.setDarkBackground(mainPanel);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(filterPanel, BorderLayout.BEFORE_FIRST_LINE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadFilters();
        loadTrains(null, null);
        setVisible(true);
    }

    private void loadFilters() {
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT source FROM trains");
            while (rs.next()) sourceFilter.addItem(rs.getString("source"));

            rs = stmt.executeQuery("SELECT DISTINCT destination FROM trains");
            while (rs.next()) destinationFilter.addItem(rs.getString("destination"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void applyFilters() {
        String src = (String) sourceFilter.getSelectedItem();
        String dest = (String) destinationFilter.getSelectedItem();
        if ("All".equals(src)) src = null;
        if ("All".equals(dest)) dest = null;
        model.setRowCount(0);
        loadTrains(src, dest);
    }

    private void loadTrains(String source, String dest) {
        try (Connection conn = DBConnection.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT * FROM trains WHERE 1=1");
            if (source != null) sql.append(" AND source = ?");
            if (dest != null) sql.append(" AND destination = ?");
            PreparedStatement stmt = conn.prepareStatement(sql.toString());

            int i = 1;
            if (source != null) stmt.setString(i++, source);
            if (dest != null) stmt.setString(i++, dest);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("train_id"),
                        rs.getString("train_name"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getTime("departure_time"),
                        rs.getTime("arrival_time"),
                        rs.getDate("date"),
                        rs.getInt("total_seats")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading trains", "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void reloadTrainTable() {
        model.setRowCount(0);
        applyFilters();
    }

    private void handleBooking(String username) {
        int row = trainTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a train first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String seatStr = JOptionPane.showInputDialog(this, "Enter number of seats to book:");
        if (seatStr == null || seatStr.isEmpty()) return;

        int seats;
        try {
            seats = Integer.parseInt(seatStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int trainId = (int) trainTable.getValueAt(row, 0);
        int available = (int) trainTable.getValueAt(row, 7);
        if (seats <= 0 || seats > available) {
            JOptionPane.showMessageDialog(this, "Invalid seat count.", "Booking Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement getUser = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            getUser.setString(1, username);
            ResultSet rsUser = getUser.executeQuery();
            int userId = rsUser.next() ? rsUser.getInt("id") : -1;

            String trainName = trainTable.getValueAt(row, 1).toString();
            String source = trainTable.getValueAt(row, 2).toString();
            String destination = trainTable.getValueAt(row, 3).toString();

            PreparedStatement insert = conn.prepareStatement(
                "INSERT INTO bookings (user_id, train_id, train_name, source, destination, seats_booked, booking_date, status) VALUES (?, ?, ?, ?, ?, ?, CURDATE(), 'Confirmed')");
            insert.setInt(1, userId);
            insert.setInt(2, trainId);
            insert.setString(3, trainName);
            insert.setString(4, source);
            insert.setString(5, destination);
            insert.setInt(6, seats);
            insert.executeUpdate();

            PreparedStatement update = conn.prepareStatement(
                "UPDATE trains SET total_seats = total_seats - ? WHERE train_id = ?");
            update.setInt(1, seats);
            update.setInt(2, trainId);
            update.executeUpdate();

            // Generate PDF
            PreparedStatement getBookingId = conn.prepareStatement(
                "SELECT booking_id FROM bookings WHERE user_id = ? AND train_id = ? ORDER BY booking_id DESC LIMIT 1");
            getBookingId.setInt(1, userId);
            getBookingId.setInt(2, trainId);
            ResultSet rsBook = getBookingId.executeQuery();

            if (rsBook.next()) {
                int bookingId = rsBook.getInt("booking_id");
                PDFGenerator.generateTicket(username, trainName, seats, java.time.LocalDate.now().toString(), bookingId);
            }

            JOptionPane.showMessageDialog(this, "✅ Booking successful!");
            reloadTrainTable();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Booking error!", "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
