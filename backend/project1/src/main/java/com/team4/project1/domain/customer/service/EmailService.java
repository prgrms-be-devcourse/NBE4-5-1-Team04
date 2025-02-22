package com.team4.project1.domain.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);       // 수신자
        message.setSubject(subject); // 제목
        message.setText(text);       // 내용
        // 필요에 따라 cc, bcc, from(기본값: application.yml username) 등 설정 가능
        javaMailSender.send(message);
    }
}
