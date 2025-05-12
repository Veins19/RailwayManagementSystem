/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package railway.gui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 *
 * @author tharu
 */
public class ViewAllTrainsFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public ViewAllTrainsFrame() {
        setTitle("All Trains");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{
            "ID", "Name", "Source", "Destination", "Departure", "Arrival", "Date", "Total Seats"
        }, 0);

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");

        refreshBtn.addActionListener(e -> loadTrains());
        editBtn.addActionListener(e -> editSelectedTrain());
        deleteBtn.addActionListener(e -> deleteSelectedTrain());

        buttonPanel.add(refreshBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        loadTrains();
    }

    private void loadTrains() {
        model.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/railway_db", "root", "Tharun@123");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM trains")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("train_id"),
                    rs.getString("train_name"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getString("departure_time"),
                    rs.getString("arrival_time"),
                    rs.getString("date"),
                    rs.getInt("total_seats"),
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading trains: " + e.getMessage());
        }
    }

    private void editSelectedTrain() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a train to edit.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);
        String source = (String) model.getValueAt(row, 2);
        String dest = (String) model.getValueAt(row, 3);
        String dep = (String) model.getValueAt(row, 4);
        String arr = (String) model.getValueAt(row, 5);
        String date = (String) model.getValueAt(row, 6);
        int total = (int) model.getValueAt(row, 7);

        new TrainEditDialog(this, id, name, source, dest, dep, arr, date, total).setVisible(true);
        loadTrains();
    }

    private void deleteSelectedTrain() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a train to delete.");
            return;
        }

        int trainId = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this train?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        AdminDashboardFrame.deleteTrain(trainId);

        // ✅ Refresh the table view
        loadTrains();
    }
}
