/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package railway.gui;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
/**
 *
 * @author tharu
 */
public class TrainEditDialog extends JDialog {
    private JTextField nameField, sourceField, destinationField, departureField, arrivalField, dateField, totalSeatsField;
    private int trainId;

    public TrainEditDialog(JFrame parent, int trainId, String name, String source, String destination,
                           String departure, String arrival, String date, int totalSeats) {
        super(parent, "Edit Train", true);
        this.trainId = trainId;
        setLayout(new GridLayout(10, 2, 10, 10));
        setSize(400, 400);
        setLocationRelativeTo(parent);

        add(new JLabel("Train Name:")); nameField = new JTextField(name); add(nameField);
        add(new JLabel("Source:")); sourceField = new JTextField(source); add(sourceField);
        add(new JLabel("Destination:")); destinationField = new JTextField(destination); add(destinationField);
        add(new JLabel("Departure Time (HH:MM:SS):")); departureField = new JTextField(departure); add(departureField);
        add(new JLabel("Arrival Time (HH:MM:SS):")); arrivalField = new JTextField(arrival); add(arrivalField);
        add(new JLabel("Date (YYYY-MM-DD):")); dateField = new JTextField(date); add(dateField);
        add(new JLabel("Total Seats:")); totalSeatsField = new JTextField(String.valueOf(totalSeats)); add(totalSeatsField);
        

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateTrain());
        add(new JLabel()); add(updateButton);
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
            JOptionPane.showMessageDialog(this, "Train updated successfully.");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating train: " + e.getMessage());
        }
    }
}
