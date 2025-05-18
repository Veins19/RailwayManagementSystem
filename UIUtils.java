package railway.gui;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UIUtils {
    public static final Color BACKGROUND = new Color(30, 30, 30);
    public static final Color TEXT_COLOR = new Color(0x00FFD1);
    public static final Color FIELD_BG = new Color(45, 45, 45);
    public static final Color BUTTON_BG = new Color(0x00FFD1);
    public static final Font DEFAULT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);

    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(BACKGROUND);
    }

    public static void stylePanel(JPanel panel) {
        panel.setBackground(BACKGROUND);
    }

    public static void styleLabel(JLabel label) {
        label.setForeground(TEXT_COLOR);
        label.setFont(DEFAULT_FONT);
    }

    public static void styleTitle(JLabel label) {
        label.setForeground(TEXT_COLOR);
        label.setFont(TITLE_FONT);
    }

    public static void styleTextField(JTextField field) {
        field.setBackground(FIELD_BG);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(DEFAULT_FONT);
        field.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
    }

    public static void stylePasswordField(JPasswordField field) {
        field.setBackground(FIELD_BG);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(DEFAULT_FONT);
        field.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
    }

    public static void styleButton(JButton button) {
        button.setBackground(BUTTON_BG);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
    }

    public static void styleTable(JTable table) {
        table.setBackground(FIELD_BG);
        table.setForeground(Color.WHITE);
        table.setFont(DEFAULT_FONT);
        table.setRowHeight(25);

        JTableHeader header = table.getTableHeader();
        header.setBackground(BACKGROUND);
        header.setForeground(TEXT_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    // NEW: Make dark label
    public static JLabel makeDarkLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_COLOR);
        label.setFont(DEFAULT_FONT);
        return label;
    }

    // NEW: Set dark background and text for any component
    public static void setDarkBackground(JComponent component) {
        component.setBackground(BACKGROUND);
        component.setForeground(TEXT_COLOR);
    }
}
