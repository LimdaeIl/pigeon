package com.house.pigeon.member.service;

import com.house.pigeon.common.exception.CustomAPIException;
import com.house.pigeon.common.exception.CustomInvalidAuthNumberException;
import com.house.pigeon.member.model.EmailCheckDTO;
import com.house.pigeon.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;

    // 이메일 인증 번호 생성
    private Integer generateRandomNumber() {
        Random random = new Random();
        StringBuilder randomNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            randomNumber.append(random.nextInt(10));
        }
        return Integer.parseInt(randomNumber.toString());
    }

    // 이메일 전송 메서드
    public void sendVerificationEmail(String email) {
        Integer authNumber = generateRandomNumber();
        String setFrom = "piay801@naver.com";
        String title = "회원 가입 인증 이메일 입니다.";
        String content = "임대일 프로젝트를 방문해주셔서 감사합니다." +
                "<br><br>" +
                "인증 번호는 " + authNumber + "입니다." +
                "<br>" +
                "인증번호를 제대로 입력해주세요";

        sendEmail(setFrom, email, title, content);
        saveAuthNumberInCache(authNumber, email);
    }

    // 이메일 전송 로직
    private void sendEmail(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new CustomAPIException("이메일 전송: 이메일 전송에 실패했습니다.");
        }
    }

    // 인증 번호를 캐시에 저장
    private void saveAuthNumberInCache(Integer authNumber, String email) {
        String authKey = authNumber.toString();
        redisUtil.setDataExpire(authKey, email, 60 * 5L); // 5분 동안 유효
    }

    // 인증 번호 확인 메서드
    public void verifyAuthNumber(EmailCheckDTO emailCheckDTO) {
        String cachedEmail = redisUtil.getData(emailCheckDTO.authNum());
        if (!emailCheckDTO.email().equals(cachedEmail)) {
            throw new CustomInvalidAuthNumberException("인증 번호: 인증 번호가 유효하지 않습니다.");
        }
    }
}