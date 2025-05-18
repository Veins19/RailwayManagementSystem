package railway.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import railway.gui.UIUtils;
import java.awt.*;
import java.sql.*;

public class ManageTrainsFrame extends JFrame {

    private JTable trainTable;
    private DefaultTableModel tableModel;

    public ManageTrainsFrame() {
        setTitle("Manage Trains");
        setSize(1000, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        UIUtils.stylePanel(mainPanel);

        JLabel title = new JLabel("Edit or Delete Trains", SwingConstants.CENTER);
        UIUtils.styleTitle(title);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        String[] columns = {
            "ID", "Name", "Source", "Destination",
            "Departure", "Arrival", "Date", "Total Seats"
        };
        tableModel = new DefaultTableModel(columns, 0);
        trainTable = new JTable(tableModel);
        UIUtils.styleTable(trainTable);

        JScrollPane scrollPane = new JScrollPane(trainTable);
        scrollPane.getViewport().setBackground(UIUtils.FIELD_BG);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        UIUtils.stylePanel(buttonPanel);

        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("🗑️ Delete Selected");

        UIUtils.styleButton(editButton);
        UIUtils.styleButton(deleteButton);

        editButton.addActionListener(e -> editSelectedTrain());
        deleteButton.addActionListener(e -> deleteSelectedTrain());

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        loadTrainsFromDatabase();
        setVisible(true);
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
            AdminDashboardFrame.deleteTrain(trainId);
            loadTrainsFromDatabase();
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
