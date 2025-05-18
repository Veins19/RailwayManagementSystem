/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package railway.db;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;

/**
 *
 * @author tharu
 */
public class BookingReportGenerator {
    public static void generatePDFReport(String outputPath) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = """
                SELECT b.booking_id, u.username, b.train_name, b.source, b.destination,
                       b.seats_booked, b.booking_date, b.status
                FROM bookings b
                JOIN users u ON b.user_id = u.id
                ORDER BY b.booking_date DESC
            """;

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            PdfWriter writer = new PdfWriter(outputPath);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc);

            Paragraph title = new Paragraph("Railway Booking Report")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(18);
            doc.add(title);
            doc.add(new Paragraph("\n"));

            Table table = new Table(new float[]{1, 2, 2, 2, 2, 1, 2, 1});

            String[] headers = {
                "Booking ID", "Username", "Train", "From", "To", "Seats", "Date", "Status"
            };
            for (String header : headers) {
                Cell headerCell = new Cell().add(new Paragraph(header)
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
                table.addHeaderCell(headerCell);
            }

            while (rs.next()) {
                table.addCell(String.valueOf(rs.getInt("booking_id")));
                table.addCell(rs.getString("username"));
                table.addCell(rs.getString("train_name"));
                table.addCell(rs.getString("source"));
                table.addCell(rs.getString("destination"));
                table.addCell(String.valueOf(rs.getInt("seats_booked")));
                table.addCell(rs.getDate("booking_date").toString());
                table.addCell(rs.getString("status"));
            }

            doc.add(table);
            doc.close();

            System.out.println("✅ PDF report saved to: " + outputPath);

            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(new File(outputPath));
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}