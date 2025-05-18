package railway.gui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TrainEditDialog extends JDialog {
    private JTextField nameField, sourceField, destinationField, departureField, arrivalField, dateField, totalSeatsField;
    private int trainId;

    public TrainEditDialog(JFrame parent, int trainId, String name, String source, String destination,
                           String departure, String arrival, String date, int totalSeats) {
        super(parent, "Edit Train", true);
        this.trainId = trainId;

        JPanel mainPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        UIUtils.stylePanel(mainPanel);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Fields
        nameField = createRow(mainPanel, "Train Name:", name);
        sourceField = createRow(mainPanel, "Source:", source);
        destinationField = createRow(mainPanel, "Destination:", destination);
        departureField = createRow(mainPanel, "Departure Time (HH:MM:SS):", departure);
        arrivalField = createRow(mainPanel, "Arrival Time (HH:MM:SS):", arrival);
        dateField = createRow(mainPanel, "Date (YYYY-MM-DD):", date);
        totalSeatsField = createRow(mainPanel, "Total Seats:", String.valueOf(totalSeats));

        // Buttons
        JButton updateBtn = new JButton("Update");
        UIUtils.styleButton(updateBtn);
        updateBtn.addActionListener(e -> updateTrain());

        JPanel buttonPanel = new JPanel();
        UIUtils.stylePanel(buttonPanel);
        buttonPanel.add(updateBtn);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(450, 400);
        setLocationRelativeTo(parent);
    }

    private JTextField createRow(JPanel panel, String label, String defaultText) {
        JLabel jLabel = new JLabel(label);
        UIUtils.styleLabel(jLabel);
        JTextField field = new JTextField(defaultText);
        UIUtils.styleTextField(field);
        panel.add(jLabel);
        panel.add(field);
        return field;
    }

    private void updateTrain() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/railway_db", "root", "Tharun@123")) {
            String sql = "UPDATE trains SET train_name=?, source=?, destination=?, departure_time=?, arrival_time=?, date=?, total_seats=? WHERE train_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nameField.getText());
            pstmt.setString(2, sourceField.getText());
            pstmt.setString(3, destinationField.getText());
            pstmt.setString(4, departureField.getText());
            pstmt.setString(5, arrivalField.getText());
            pstmt.setString(6, dateField.getText());
            pstmt.setInt(7, Integer.parseInt(totalSeatsField.getText()));
            pstmt.setInt(8, trainId);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Train updated successfully!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error updating train: " + e.getMessage());
        }
    }
}
