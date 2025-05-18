package railway.gui;

import railway.db.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class AddTrainFrame extends JFrame {
    private JTextField nameField, sourceField, destinationField, departureField, arrivalField, dateField, seatsField;

    public AddTrainFrame() {
        setTitle("Add New Train");
        setSize(550, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main container panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        UIUtils.stylePanel(mainPanel);

        // Header
        JLabel heading = new JLabel("Add New Train Details", SwingConstants.CENTER);
        UIUtils.styleTitle(heading);
        mainPanel.add(heading, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 15, 15));
        UIUtils.stylePanel(formPanel);

        Font labelFont = UIUtils.DEFAULT_FONT;

        nameField = createStyledField("Train Name:", formPanel, labelFont);
        sourceField = createStyledField("Source:", formPanel, labelFont);
        destinationField = createStyledField("Destination:", formPanel, labelFont);
        departureField = createStyledField("Departure Time (HH:MM:SS):", formPanel, labelFont);
        arrivalField = createStyledField("Arrival Time (HH:MM:SS):", formPanel, labelFont);
        dateField = createStyledField("Date (YYYY-MM-DD):", formPanel, labelFont);
        seatsField = createStyledField("Total Seats:", formPanel, labelFont);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        UIUtils.stylePanel(buttonPanel);

        JButton addButton = new JButton("Add Train");
        JButton cancelButton = new JButton("Cancel");

        UIUtils.styleButton(addButton);
        UIUtils.styleButton(cancelButton);
        addButton.setBackground(new Color(0x00FFD1));
        cancelButton.setBackground(new Color(0xFF6666));

        addButton.addActionListener(e -> addTrainToDB());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JTextField createStyledField(String labelText, JPanel panel, Font font) {
        JLabel label = new JLabel(labelText);
        UIUtils.styleLabel(label);

        JTextField field = new JTextField();
        UIUtils.styleTextField(field);

        panel.add(label);
        panel.add(field);
        return field;
    }

    private void addTrainToDB() {
        String name = nameField.getText().trim();
        String source = sourceField.getText().trim();
        String dest = destinationField.getText().trim();
        String dep = departureField.getText().trim();
        String arr = arrivalField.getText().trim();
        String date = dateField.getText().trim();
        String seatsText = seatsField.getText().trim();

        if (name.isEmpty() || source.isEmpty() || dest.isEmpty() || dep.isEmpty() || arr.isEmpty() || date.isEmpty() || seatsText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int totalSeats = Integer.parseInt(seatsText);

            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/railway_db", "root", "Tharun@123");
            String sql = "INSERT INTO trains (train_name, source, destination, departure_time, arrival_time, date, total_seats) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, source);
            stmt.setString(3, dest);
            stmt.setString(4, dep);
            stmt.setString(5, arr);
            stmt.setString(6, date);
            stmt.setInt(7, totalSeats);

            stmt.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(this, "Train added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for total seats.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding train: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
