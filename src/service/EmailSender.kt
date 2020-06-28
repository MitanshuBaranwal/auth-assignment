package com.assignment.service

import com.assignment.mailer
import org.simplejavamail.email.EmailBuilder


fun sendEmail(otp: String, email: String) {
    val email = EmailBuilder.startingBlank()
        .from("test", "test@example.com")
        .to("user", email)
        .withSubject("hey")
        .withPlainText("Your OTP ${otp}")
        .buildEmail()
    mailer.sendMail(email)
}