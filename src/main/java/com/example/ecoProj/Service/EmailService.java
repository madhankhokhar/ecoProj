package com.example.ecoProj.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async("emailExecutor")
    public CompletableFuture<String> sendRegistrationEmail(
            String toEmail,
            String username) {

        try {

            System.out.println(
                    "Running Thread: " +
                            Thread.currentThread().getName()
            );

            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setTo(toEmail);

            message.setSubject(
                    "Registration Successful"
            );

            message.setText(
                    "Hello " + username +
                            ", your account has been created successfully."
            );

            mailSender.send(message);

            return CompletableFuture.completedFuture(
                    "Email Sent Successfully"
            );

        } catch (Exception e) {

            return CompletableFuture.completedFuture(
                    "Email Failed: " + e.getMessage()
            );
        }
    }
}