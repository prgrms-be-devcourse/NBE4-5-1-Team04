package com.team4.project1.global.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    void testSendSimpleEmail() {
        // 실제 발송 테스트

//        username: ${MAIL_USERNAME}
//        password: ${MAIL_PASSWORD}
        String to = "수신자_이메일@example.com";
        String subject = "테스트 메일 제목";
        String text = "테스트 메일 내용입니다.";

        emailService.sendSimpleEmail(to, subject, text);

        // 실제로 수신자 메일함에서 확인 필요
        System.out.println("이메일 발송 테스트 완료 - 메일함을 확인하세요.");
    }
}
