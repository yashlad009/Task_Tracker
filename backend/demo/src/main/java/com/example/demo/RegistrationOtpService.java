package com.example.demo;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RegistrationOtpService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    @Value("${spring.mail.password:}")
    private String senderPassword;

    private final ConcurrentHashMap<String, String> otpStorage = new ConcurrentHashMap<>();

    public String generateAndSaveOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(1000000));
        otpStorage.put(email, otp);
        return otp;
    }

    public boolean isEmailConfigured() {
        return senderEmail != null && !senderEmail.isBlank();
    }

    public boolean verifyOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStorage.remove(email);
            return true;
        }
        return false;
    }

    public void sendOtpEmail(String toEmail, String otp) {
        if (senderEmail == null || senderEmail.isBlank()) {
            throw new IllegalStateException("MAIL_USERNAME is missing. Set your Gmail address in the environment.");
        }
        if (senderPassword == null || senderPassword.isBlank()) {
            throw new IllegalStateException("MAIL_PASSWORD is missing. Use your Gmail app password, not your normal Gmail password.");
        }

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("APMS PRO | Security Verification");

            String htmlContent =
                    "<html>" +
                            "<body style='margin: 0; padding: 0; background-color: #0c0c14; font-family: \"Segoe UI\", Arial, sans-serif;'>" +
                            "<table width='100%' border='0' cellspacing='0' cellpadding='0' style='padding: 50px 0;'>" +
                            "<tr>" +
                            "<td align='center'>" +
                            "<table width='550' border='0' cellspacing='0' cellpadding='0' style='background-color: #161625; border-radius: 24px; border: 1px solid #2a2a3d; overflow: hidden;'>" +
                            "<tr>" +
                            "<td style='padding: 35px; background: linear-gradient(to right, #1a1a2e, #161625); text-align: center; border-bottom: 1px solid #2a2a3d;'>" +
                            "<h2 style='margin: 0; color: #00d2ff; font-size: 22px; letter-spacing: 2px; text-transform: uppercase;'>APMS PRO</h2>" +
                            "</td>" +
                            "</tr>" +
                            "<tr>" +
                            "<td style='padding: 40px 40px 30px 40px; text-align: center; color: #ffffff;'>" +
                            "<p style='font-size: 16px; opacity: 0.9; margin-bottom: 20px;'>Security Verification</p>" +
                            "<p style='font-size: 15px; line-height: 1.6; color: #b0b0c0;'>To finalize your account setup, please enter the unique 6-digit code provided below into the registration screen.</p>" +
                            "<div style='margin: 35px 0; padding: 25px; background-color: rgba(0, 210, 255, 0.05); border: 1px solid rgba(0, 210, 255, 0.2); border-radius: 16px;'>" +
                            "<span style='font-size: 42px; font-weight: 800; color: #ffffff; letter-spacing: 10px;'>" + otp + "</span>" +
                            "</div>" +
                            "<p style='font-size: 12px; color: #6e6e8e;'>This code expires in <span style='color: #ffffff;'>10 minutes</span>. For your safety, do not share this with anyone.</p>" +
                            "</td>" +
                            "</tr>" +
                            "<tr>" +
                            "<td style='padding: 0 40px 40px 40px;'>" +
                            "<div style='background: rgba(255, 255, 255, 0.02); padding: 20px; border-radius: 15px; border: 1px solid #2a2a3d;'>" +
                            "<p style='margin: 0 0 12px 0; font-size: 14px; font-weight: 600; color: #00d2ff;'>Unlock Your Workspace:</p>" +
                            "<table width='100%' border='0' cellspacing='0' cellpadding='4' style='font-size: 13px; color: #b0b0c0;'>" +
                            "<tr><td width='20' style='color:#9d50bb;'>&bull;</td><td>Personalized Task Dashboard</td></tr>" +
                            "<tr><td width='20' style='color:#9d50bb;'>&bull;</td><td>Milestone Performance Tracking</td></tr>" +
                            "<tr><td width='20' style='color:#9d50bb;'>&bull;</td><td>Real-time Admin Support Messaging</td></tr>" +
                            "</table>" +
                            "</div>" +
                            "</td>" +
                            "</tr>" +
                            "<tr>" +
                            "<td style='padding: 25px; background-color: #0e0e1a; text-align: center; border-top: 1px solid #2a2a3d;'>" +
                            "<p style='margin: 0; font-size: 11px; color: #5c5c7a; line-height: 1.6;'>" +
                            "APMS PRO Productivity Engine | Pune, India<br>" +
                            "This is an automated security email. Please do not reply." +
                            "</p>" +
                            "</td>" +
                            "</tr>" +
                            "</table>" +
                            "</td>" +
                            "</tr>" +
                            "</table>" +
                            "</body>" +
                            "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to send OTP email: " + e.getMessage(), e);
        }
    }
}
