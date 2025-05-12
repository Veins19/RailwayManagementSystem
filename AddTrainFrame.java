/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package railway.gui;
import railway.db.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.*;
/**
 *
 * @author tharu
 */
public class AddTrainFrame extends JFrame {
    private JTextField nameField, sourceField, destinationField, departureField, arrivalField, dateField, seatsField;

    public AddTrainFrame() {
        setTitle("Add New Train");
        setSize(550, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Use a container panel for padding and layout
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(245, 248, 250)); // light background

        // Header
        JLabel heading = new JLabel("Add New Train Details", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setForeground(new Color(40, 75, 99));
        mainPanel.add(heading, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 15, 15));
        formPanel.setBackground(new Color(245, 248, 250));
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);

        // Helper method to create label and field
        nameField = createLabeledField("Train Name:", formPanel, labelFont);
        sourceField = createLabeledField("Source:", formPanel, labelFont);
        destinationField = createLabeledField("Destination:", formPanel, labelFont);
        departureField = createLabeledField("Departure Time (HH:MM:SS):", formPanel, labelFont);
        arrivalField = createLabeledField("Arrival Time (HH:MM:SS):", formPanel, labelFont);
        dateField = createLabeledField("Date (YYYY-MM-DD):", formPanel, labelFont);
        seatsField = createLabeledField("Total Seats:", formPanel, labelFont);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonPanel.setBackground(new Color(245, 248, 250));

        JButton addButton = new JButton("Add Train");
        JButton cancelButton = new JButton("Cancel");

        stylizeButton(addButton, new Color(0, 123, 255), Color.WHITE);
        stylizeButton(cancelButton, new Color(220, 53, 69), Color.WHITE);

        addButton.addActionListener(e -> addTrainToDB());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JTextField createLabeledField(String labelText, JPanel panel, Font font) {
        JLabel label = new JLabel(labelText);
        label.setFont(font);
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panel.add(label);
        panel.add(field);
        return field;
    }

    private void stylizeButton(JButton button, Color bgColor, Color fgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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