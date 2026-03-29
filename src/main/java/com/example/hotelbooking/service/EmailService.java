package com.example.hotelbooking.service;

import com.example.hotelbooking.model.Booking;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:Luxury Stay}")
    private String appName;

    public void sendBookingConfirmation(Booking booking, String toEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(appName + " - Booking Confirmed! #" + booking.getId().substring(0, 8).toUpperCase());
            helper.setText(buildEmailHtml(booking), true);

            mailSender.send(message);
            System.out.println("Confirmation email sent to: " + toEmail);

        } catch (MessagingException e) {
            // ✅ Catches email formatting/sending errors
            System.err.println("Email sending failed (messaging): " + e.getMessage());
        } catch (Exception e) {
            // ✅ Catches MailAuthenticationException and any other errors
            // This prevents email failure from crashing the booking
            System.err.println("Email sending failed (auth/other): " + e.getMessage());
        }
    }

    private String buildEmailHtml(Booking booking) {
        long nights = 1;
        try {
            LocalDate in  = LocalDate.parse(booking.getCheckInDate());
            LocalDate out = LocalDate.parse(booking.getCheckOutDate());
            nights = ChronoUnit.DAYS.between(in, out);
        } catch (Exception ignored) {}

        String hotelName     = booking.getRoom().getName();
        String checkIn       = booking.getCheckInDate();
        String checkOut      = booking.getCheckOutDate() != null ? booking.getCheckOutDate() : "N/A";
        String guestName     = booking.getUser().getUsername();
        String bookingRef    = booking.getId().substring(0, 8).toUpperCase();
        double pricePerNight = booking.getRoom().getPrice();
        double totalCost     = booking.getTotalCost();

        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <style>
                body { font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 0; }
                .wrapper { max-width: 600px; margin: 30px auto; background: white;
                           border-radius: 16px; overflow: hidden;
                           box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                .header { background: linear-gradient(135deg, #0d47a1, #1976d2);
                          color: white; padding: 40px 30px; text-align: center; }
                .header h1 { margin: 0; font-size: 28px; letter-spacing: 1px; }
                .header p  { margin: 8px 0 0; opacity: 0.85; font-size: 15px; }
                .badge { display: inline-block; background: #4caf50; color: white;
                         padding: 6px 20px; border-radius: 20px; font-size: 13px;
                         font-weight: bold; margin-top: 16px; }
                .body { padding: 32px 30px; }
                .greeting { font-size: 18px; color: #1e293b; margin-bottom: 20px; }
                .details-box { background: #f8faff; border: 1px solid #dbeafe;
                               border-radius: 12px; padding: 24px; margin-bottom: 24px; }
                .details-box h3 { margin: 0 0 16px; color: #0d47a1; font-size: 16px; }
                .detail-row { display: flex; justify-content: space-between;
                              padding: 8px 0; border-bottom: 1px solid #e2e8f0;
                              font-size: 14px; color: #475569; }
                .detail-row:last-child { border-bottom: none; }
                .detail-row .label { font-weight: 600; color: #1e293b; }
                .total-row { background: #eff6ff; border-radius: 10px; padding: 14px 18px;
                             display: flex; justify-content: space-between;
                             font-size: 17px; font-weight: bold; color: #0d47a1;
                             margin-bottom: 24px; }
                .note { font-size: 13px; color: #64748b; background: #f8f9fa;
                        border-radius: 8px; padding: 14px; line-height: 1.6; }
                .footer { background: #0d47a1; color: white; text-align: center;
                          padding: 20px; font-size: 13px; opacity: 0.9; }
              </style>
            </head>
            <body>
              <div class="wrapper">
                <div class="header">
                  <h1>&#127970; Luxury Stay</h1>
                  <p>Your reservation is confirmed</p>
                  <div class="badge">&#10003; BOOKING CONFIRMED</div>
                </div>
                <div class="body">
                  <p class="greeting">Hi <strong>%s</strong>, thank you for your booking!</p>
                  <div class="details-box">
                    <h3>&#128203; Booking Details</h3>
                    <div class="detail-row"><span class="label">Booking Reference</span><span>#%s</span></div>
                    <div class="detail-row"><span class="label">Hotel</span><span>%s</span></div>
                    <div class="detail-row"><span class="label">Check-in</span><span>%s</span></div>
                    <div class="detail-row"><span class="label">Check-out</span><span>%s</span></div>
                    <div class="detail-row"><span class="label">Duration</span><span>%d night(s)</span></div>
                    <div class="detail-row"><span class="label">Price per Night</span><span>$%.2f</span></div>
                  </div>
                  <div class="total-row">
                    <span>Total Paid</span>
                    <span>$%.2f</span>
                  </div>
                  <div class="note">
                    &#128274; <strong>Free cancellation</strong> is available from your
                    "My Bookings" page.<br>
                    &#128236; A PDF receipt is available to download from your bookings page.<br>
                    &#128222; For assistance, reply to this email.
                  </div>
                </div>
                <div class="footer">
                  &copy; 2026 Luxury Stay &bull; This is an automated confirmation email
                </div>
              </div>
            </body>
            </html>
            """.formatted(guestName, bookingRef, hotelName, checkIn, checkOut,
                          nights, pricePerNight, totalCost);
    }
}