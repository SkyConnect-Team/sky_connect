package com.skyconnect.demo.controller;

import com.skyconnect.demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;


    // ==========================================
    // Test Email
    // ==========================================
    @PostMapping("/test")
    public ResponseEntity<String> sendTestEmail(@RequestParam String email) {

        boolean sent = emailService.sendEmail(
                email,
                "SkyConnect Email Test",
                "Hello!\n\n" +
                        "This is a test email from SkyConnect.\n\n" +
                        "Gmail SMTP configuration is working successfully.\n\n" +
                        "Regards,\n" +
                        "SkyConnect Team"
        );

        if (sent) {
            return ResponseEntity.ok(
                    "Test email sent successfully to " + email
            );
        }

        return ResponseEntity.status(500).body(
                "Failed to send test email to " + email
        );
    }
}