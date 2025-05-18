package railway.db;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class PDFGenerator {
    public static void generateTicket(String username, String trainName, int seatsBooked, String bookingDate, int bookingId) {
        try {
            String fileName = "Ticket_" + bookingId + ".pdf";
            String outputPath = "tickets/" + fileName;

            File dir = new File("tickets");
            if (!dir.exists()) dir.mkdirs();

            PdfWriter writer = new PdfWriter(outputPath);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            // Simple "centered" layout using dashes and padding
            doc.add(new Paragraph("********************************************").setFont(regular));
            doc.add(new Paragraph("        RAILWAY RESERVATION TICKET").setFont(bold).setFontSize(14));
            doc.add(new Paragraph("         Booking Confirmation").setFont(bold).setFontSize(12));
            doc.add(new Paragraph("********************************************").setFont(regular));
            doc.add(new Paragraph("\n"));

            doc.add(makeLine("Booking ID", String.valueOf(bookingId), bold, regular));
            doc.add(makeLine("Passenger", username, bold, regular));
            doc.add(makeLine("Train Name", trainName, bold, regular));
            doc.add(makeLine("Seats Booked", String.valueOf(seatsBooked), bold, regular));
            doc.add(makeLine("Booking Date", bookingDate, bold, regular));
            doc.add(new Paragraph("\n"));

            doc.add(new Paragraph("--------------------------------------------").setFont(regular));
            doc.add(new Paragraph("✅ Thank you for booking with us!")
                    .setFont(regular).setFontSize(11));
            doc.add(new Paragraph("Please carry a valid ID proof during travel.")
                    .setFont(regular).setFontSize(9));
            doc.add(new Paragraph("--------------------------------------------").setFont(regular));

            doc.close();

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(outputPath));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Paragraph makeLine(String label, String value, PdfFont bold, PdfFont regular) {
        return new Paragraph()
                .add(new Text(label + ": ").setFont(bold).setFontSize(12))
                .add(new Text(value).setFont(regular).setFontSize(12));
    }
}
