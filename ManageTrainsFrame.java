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
public class ManageTrainsFrame extends JFrame {

    private JTable trainTable;
    private DefaultTableModel tableModel;

    public ManageTrainsFrame() {
        setTitle("Manage Trains");
        setSize(1000, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Edit or Delete Trains", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(25, 118, 210));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        String[] columns = {
            "ID", "Name", "Source", "Destination",
            "Departure", "Arrival", "Date", "Total Seats"
        };
        tableModel = new DefaultTableModel(columns, 0);
        trainTable = new JTable(tableModel);
        trainTable.setRowHeight(25);
        trainTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        trainTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(trainTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");

        styleButton(editButton, new Color(255, 143, 0));
        styleButton(deleteButton, new Color(229, 57, 53));

        editButton.addActionListener(e -> editSelectedTrain());
        deleteButton.addActionListener(e -> deleteSelectedTrain());

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadTrainsFromDatabase();
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(160, 40));
    }

    private void loadTrainsFromDatabase() {
        tableModel.setRowCount(0);
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/railway_db", "root", "Tharun@123");
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM trains")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("train_id"),
                    rs.getString("train_name"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getString("departure_time"),
                    rs.getString("arrival_time"),
                    rs.getDate("date"),
                    rs.getInt("total_seats"),
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage());
        }
    }

    private void deleteSelectedTrain() {
        int selectedRow = trainTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a train to delete.");
            return;
        }

        int trainId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Train ID: " + trainId + "?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/railway_db", "root", "Tharun@123");
                 PreparedStatement ps = con.prepareStatement("DELETE FROM trains WHERE train_id = ?")) {

                ps.setInt(1, trainId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Train deleted successfully.");
                loadTrainsFromDatabase();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting train: " + ex.getMessage());
            }
        }
    }

    private void editSelectedTrain() {
        int selectedRow = trainTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a train to edit.");
            return;
        }

        int trainId = (int) tableModel.getValueAt(selectedRow, 0);
        String newName = JOptionPane.showInputDialog(this, "Enter new name:",
                tableModel.getValueAt(selectedRow, 1));
        if (newName == null || newName.trim().isEmpty()) return;

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/railway_db", "root", "Tharun@123");
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE trains SET train_name = ? WHERE train_id = ?")) {

            ps.setString(1, newName.trim());
            ps.setInt(2, trainId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Train updated successfully.");
            loadTrainsFromDatabase();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating train: " + ex.getMessage());
        }
    }
}