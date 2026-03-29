package com.example.hotelbooking.service;

import com.example.hotelbooking.model.Booking;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class PdfReceiptService {

    // Colour palette
    private static final BaseColor NAVY      = new BaseColor(13,  71,  161);
    private static final BaseColor LIGHT_BLUE= new BaseColor(227, 242, 253);
    private static final BaseColor WHITE     = BaseColor.WHITE;
    private static final BaseColor LIGHT_GREY= new BaseColor(245, 245, 245);
    private static final BaseColor DARK_TEXT = new BaseColor(30,  41,  59);
    private static final BaseColor MUTED     = new BaseColor(100, 116, 139);

    // Fonts
    private static final Font TITLE_FONT    = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD,  WHITE);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, WHITE);
    private static final Font SECTION_FONT  = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD,  NAVY);
    private static final Font LABEL_FONT    = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD,  DARK_TEXT);
    private static final Font VALUE_FONT    = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL,DARK_TEXT);
    private static final Font MUTED_FONT    = new Font(Font.FontFamily.HELVETICA,  9, Font.NORMAL,MUTED);
    private static final Font TOTAL_LABEL   = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD,  NAVY);
    private static final Font TOTAL_VALUE   = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD,  NAVY);
    private static final Font STATUS_FONT   = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD,  WHITE);

    public byte[] generateReceipt(Booking booking) throws DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(doc, out);
        doc.open();

        // ── HEADER BANNER ────────────────────────────────────────────────────
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);

        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(NAVY);
        headerCell.setPadding(28);
        headerCell.setBorder(Rectangle.NO_BORDER);

        Paragraph brand = new Paragraph("🏨  LUXURY STAY", TITLE_FONT);
        brand.setAlignment(Element.ALIGN_CENTER);
        headerCell.addElement(brand);

        Paragraph sub = new Paragraph("Official Booking Receipt", SUBTITLE_FONT);
        sub.setAlignment(Element.ALIGN_CENTER);
        headerCell.addElement(sub);

        header.addCell(headerCell);
        doc.add(header);
        doc.add(new Paragraph(" "));

        // ── STATUS BADGE ─────────────────────────────────────────────────────
        PdfPTable statusTable = new PdfPTable(1);
        statusTable.setWidthPercentage(40);
        statusTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell statusCell = new PdfPCell(new Phrase("✔  BOOKING CONFIRMED", STATUS_FONT));
        statusCell.setBackgroundColor(new BaseColor(76, 175, 80));
        statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        statusCell.setPadding(8);
        statusCell.setBorder(Rectangle.NO_BORDER);
        statusTable.addCell(statusCell);
        doc.add(statusTable);
        doc.add(new Paragraph(" "));

        // ── BOOKING REFERENCE BOX ────────────────────────────────────────────
        String bookingRef = booking.getId().substring(0, 8).toUpperCase();
        PdfPTable refTable = new PdfPTable(1);
        refTable.setWidthPercentage(100);

        PdfPCell refCell = new PdfPCell();
        refCell.setBackgroundColor(LIGHT_BLUE);
        refCell.setPadding(12);
        refCell.setBorder(Rectangle.NO_BORDER);

        Paragraph refPara = new Paragraph("Booking Reference:  #" + bookingRef, SECTION_FONT);
        refPara.setAlignment(Element.ALIGN_CENTER);
        refCell.addElement(refPara);
        refTable.addCell(refCell);
        doc.add(refTable);
        doc.add(new Paragraph(" "));

        // ── DETAILS TABLE ────────────────────────────────────────────────────
        long nights = 1;
        try {
            LocalDate in          = LocalDate.parse(booking.getCheckInDate());
            LocalDate checkOutDate = LocalDate.parse(booking.getCheckOutDate());
            nights = ChronoUnit.DAYS.between(in, checkOutDate);
        } catch (Exception ignored) {}

        PdfPTable details = new PdfPTable(2);
        details.setWidthPercentage(100);
        details.setWidths(new float[]{40f, 60f});
        details.setSpacingBefore(4);

        addDetailRow(details, "Guest Name",       booking.getUser().getUsername(), false);
        addDetailRow(details, "Hotel / Room",     booking.getRoom().getName(),     true);
        addDetailRow(details, "Check-in Date",    booking.getCheckInDate(),        false);
        addDetailRow(details, "Check-out Date",   booking.getCheckOutDate() != null
                                                  ? booking.getCheckOutDate() : "N/A", true);
        addDetailRow(details, "Duration",         nights + " night(s)",            false);
        addDetailRow(details, "Price per Night",  "$" + String.format("%.2f", booking.getRoom().getPrice()), true);
        addDetailRow(details, "Booked On",        booking.getBookingDate() != null
                                                  ? booking.getBookingDate().toString().substring(0,16) : "N/A", false);
        doc.add(details);
        doc.add(new Paragraph(" "));

        // ── TOTAL COST BOX ───────────────────────────────────────────────────
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(100);
        totalTable.setWidths(new float[]{50f, 50f});

        PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL PAID", TOTAL_LABEL));
        totalLabelCell.setBackgroundColor(LIGHT_BLUE);
        totalLabelCell.setPadding(14);
        totalLabelCell.setBorder(Rectangle.NO_BORDER);
        totalLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell totalValueCell = new PdfPCell(
            new Phrase("$" + String.format("%.2f", booking.getTotalCost()), TOTAL_VALUE));
        totalValueCell.setBackgroundColor(LIGHT_BLUE);
        totalValueCell.setPadding(14);
        totalValueCell.setBorder(Rectangle.NO_BORDER);
        totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        totalTable.addCell(totalLabelCell);
        totalTable.addCell(totalValueCell);
        doc.add(totalTable);
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(" "));

        // ── FOOTER NOTE ──────────────────────────────────────────────────────
        Paragraph note = new Paragraph(
            "Thank you for choosing Luxury Stay. We look forward to welcoming you!\n" +
            "For support or cancellations, log in to your account at any time.",
            MUTED_FONT);
        note.setAlignment(Element.ALIGN_CENTER);
        doc.add(note);

        // Divider line
        doc.add(new Paragraph(" "));
        PdfPTable divider = new PdfPTable(1);
        divider.setWidthPercentage(100);
        PdfPCell divCell = new PdfPCell();
        divCell.setBackgroundColor(NAVY);
        divCell.setFixedHeight(3f);
        divCell.setBorder(Rectangle.NO_BORDER);
        divider.addCell(divCell);
        doc.add(divider);

        Paragraph footer = new Paragraph("\n© 2026 Luxury Stay  •  This is an official booking receipt", MUTED_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);

        doc.close();
        return out.toByteArray();
    }

    private void addDetailRow(PdfPTable table, String label, String value, boolean shaded) {
        BaseColor bg = shaded ? LIGHT_GREY : WHITE;

        PdfPCell labelCell = new PdfPCell(new Phrase(label, LABEL_FONT));
        labelCell.setBackgroundColor(bg);
        labelCell.setPadding(10);
        labelCell.setBorderColor(new BaseColor(226, 232, 240));

        PdfPCell valueCell = new PdfPCell(new Phrase(value, VALUE_FONT));
        valueCell.setBackgroundColor(bg);
        valueCell.setPadding(10);
        valueCell.setBorderColor(new BaseColor(226, 232, 240));

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}