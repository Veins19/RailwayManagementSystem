/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package railway.gui;
import railway.db.DBConnection;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableModel;
import railway.db.PDFGenerator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
/**
 *
 * @author tharu
 */
public class DashboardFrame extends JFrame {
    private JTable trainTable;
    private JComboBox<String> sourceFilter, destinationFilter;
    private DefaultTableModel model;

    public DashboardFrame(String username) {
        setTitle("Dashboard - Welcome " + username);
        setSize(800, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Title
        JLabel titleLabel = new JLabel("Available Trains");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table setup
        String[] columns = {"Train ID", "Train Name", "Source", "Destination", "Departure", "Arrival", "Date", "Seats"};
        model = new DefaultTableModel(columns, 0);
        trainTable = new JTable(model);
        
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        trainTable.setRowSorter(sorter);
        
        JScrollPane scrollPane = new JScrollPane(trainTable);

        // Filters
        sourceFilter = new JComboBox<>();
        destinationFilter = new JComboBox<>();
        sourceFilter.addItem("All");
        destinationFilter.addItem("All");

        sourceFilter.addActionListener(e -> applyFilters());
        destinationFilter.addActionListener(e -> applyFilters());

        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Source:"));
        filterPanel.add(sourceFilter);
        filterPanel.add(new JLabel("Destination:"));
        filterPanel.add(destinationFilter);
        
        // Cancellation History Button
        JButton cancelHistoryButton = new JButton("Cancellation History");
        cancelHistoryButton.setBackground(new Color(0xEADFFF));
        cancelHistoryButton.setFocusPainted(false);
        cancelHistoryButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelHistoryButton.addActionListener(e -> new CancellationHistoryFrame(username));


        // Buttons
        JButton bookButton = new JButton("Book Ticket");
        JButton cancelButton = new JButton("Cancel Booking");
        JButton historyButton = new JButton("View Booking History");
        JButton logoutButton = new JButton("Logout");

        bookButton.setBackground(new Color(0x00FFD1));
        cancelButton.setBackground(new Color(0xFFDADA));
        historyButton.setBackground(new Color(0xD1EFFF));
        logoutButton.setBackground(new Color(0xFFB2B2));

        bookButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        historyButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        bookButton.addActionListener(e -> handleBooking(username));
        cancelButton.addActionListener(e -> new CancelBookingFrame(username, this));
        historyButton.addActionListener(e -> new BookingHistoryFrame(username));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(bookButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(cancelHistoryButton);
        buttonPanel.add(logoutButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
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
            while (rs.next()) {
                sourceFilter.addItem(rs.getString("source"));
            }
            rs = stmt.executeQuery("SELECT DISTINCT destination FROM trains");
            while (rs.next()) {
                destinationFilter.addItem(rs.getString("destination"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void applyFilters() {
        String selectedSource = (String) sourceFilter.getSelectedItem();
        String selectedDest = (String) destinationFilter.getSelectedItem();
        if ("All".equals(selectedSource)) selectedSource = null;
        if ("All".equals(selectedDest)) selectedDest = null;
        model.setRowCount(0);
        loadTrains(selectedSource, selectedDest);
    }

    private void loadTrains(String sourceFilter, String destFilter) {
        try (Connection conn = DBConnection.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT * FROM trains WHERE 1=1");
            if (sourceFilter != null) sql.append(" AND source = ?");
            if (destFilter != null) sql.append(" AND destination = ?");
            PreparedStatement stmt = conn.prepareStatement(sql.toString());

            int index = 1;
            if (sourceFilter != null) stmt.setString(index++, sourceFilter);
            if (destFilter != null) stmt.setString(index++, destFilter);

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
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading trains", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void reloadTrainTable() {
        model.setRowCount(0);
        applyFilters();
    }

    private void handleBooking(String username) {
        int selectedRow = trainTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a train first.", "No Train Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String seatsStr = JOptionPane.showInputDialog(this, "Enter number of seats to book:");
        if (seatsStr == null || seatsStr.isEmpty()) return;

        int seatsToBook;
        try {
            seatsToBook = Integer.parseInt(seatsStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number entered.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int trainId = (int) trainTable.getValueAt(selectedRow, 0);
        int availableSeats = (int) trainTable.getValueAt(selectedRow, 7);

        if (seatsToBook > availableSeats || seatsToBook <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid seat count. Not enough seats available.", "Booking Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Get user ID
            PreparedStatement getUser = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            getUser.setString(1, username);
            ResultSet rsUser = getUser.executeQuery();
            int userId = -1;
            if (rsUser.next()) userId = rsUser.getInt("id");

            // Extract train info
            String trainName = trainTable.getValueAt(selectedRow, 1).toString();
            String source = trainTable.getValueAt(selectedRow, 2).toString();
            String destination = trainTable.getValueAt(selectedRow, 3).toString();

            // Insert booking
            String sql = "INSERT INTO bookings (user_id, train_id, train_name, source, destination, seats_booked, booking_date, status) VALUES (?, ?, ?, ?, ?, ?, CURDATE(), 'Confirmed')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, trainId);
            stmt.setString(3, trainName);
            stmt.setString(4, source);
            stmt.setString(5, destination);
            stmt.setInt(6, seatsToBook);
            int rows = stmt.executeUpdate();

            // Update seat count
            PreparedStatement updateTrain = conn.prepareStatement(
                "UPDATE trains SET total_seats = total_seats - ? WHERE train_id = ?");
            updateTrain.setInt(1, seatsToBook);
            updateTrain.setInt(2, trainId);
            updateTrain.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Booking successful!");
                reloadTrainTable();
            }

            // Generate PDF
            String bookingDate = java.time.LocalDate.now().toString();
            PreparedStatement getBookingId = conn.prepareStatement(
                "SELECT booking_id FROM bookings WHERE user_id = ? AND train_id = ? ORDER BY booking_id DESC LIMIT 1");
            getBookingId.setInt(1, userId);
            getBookingId.setInt(2, trainId);
            ResultSet rsBooking = getBookingId.executeQuery();

            if (rsBooking.next()) {
                int bookingId = rsBooking.getInt("booking_id");
                PDFGenerator.generateTicket(username, trainName, seatsToBook, bookingDate, bookingId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during booking.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
