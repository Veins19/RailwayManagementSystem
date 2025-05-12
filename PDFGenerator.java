/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package railway.db;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.io.font.constants.StandardFonts;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
/**
 *
 * @author tharu
 */
public class PDFGenerator {
    public static void generateTicket(String username, String trainName, int seatsBooked, String bookingDate, int bookingId) {
        try {
            String fileName = "Ticket_" + bookingId + ".pdf";
            String outputPath = "tickets/" + fileName;

            // Ensure the directory exists
            File directory = new File("tickets");
            if (!directory.exists()) directory.mkdirs();

            PdfWriter writer = new PdfWriter(outputPath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Ticket content
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            Text boldText = new Text("").setFont(boldFont);
            document.add(new Paragraph("Railway Reservation Ticket").add(boldText).setFontSize(16));
            document.add(new Paragraph("Booking ID: " + bookingId));
            document.add(new Paragraph("Username: " + username));
            document.add(new Paragraph("Train Name: " + trainName));
            document.add(new Paragraph("Seats Booked: " + seatsBooked));
            document.add(new Paragraph("Booking Date: " + bookingDate));
            document.add(new Paragraph("\nThank you for booking with Railway Management System!"));

            document.close();

            System.out.println("Ticket PDF generated: " + outputPath);
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(new File(outputPath));
                    System.out.println("Ticket downloaded and opened successfully");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
