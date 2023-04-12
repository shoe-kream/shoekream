package com.shoekream.service;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.dao.CertificationNumberDao;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static com.shoekream.common.util.constants.EmailConstants.DOMAIN_NAME;
import static com.shoekream.common.util.constants.EmailConstants.MAIL_TITLE_CERTIFICATION;

@RequiredArgsConstructor
@Service
public class EmailCertificationService {

    private final JavaMailSender mailSender;
    private final CertificationNumberDao certificationNumberDao;

    public void sendEmailForCertification(String email) throws NoSuchAlgorithmException, MessagingException {

        String certificationNumber = getCertificationNumber();

        String content = String.format("%s/api/v1/users/verify?certificationNumber=%s&email=%s   링크를 3분 이내에 클릭해주세요.", DOMAIN_NAME, certificationNumber, email);
        sendMail(email, content);
        certificationNumberDao.saveCertificationNumber(email, certificationNumber);
    }

    private static String getCertificationNumber() throws NoSuchAlgorithmException {
        String result;

        do {
            int i = SecureRandom.getInstanceStrong().nextInt(999999);
            result = String.valueOf(i);
        } while (result.length() != 6);

        return result;
    }

    private void sendMail(String email, String content) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setTo(email);
        helper.setSubject(MAIL_TITLE_CERTIFICATION);
        helper.setText(content);
        mailSender.send(mimeMessage);
    }

    public void verifyEmail(String certificationNumber, String email) {
        if (isVerify(certificationNumber, email)) {
            throw new ShoeKreamException(ErrorCode.VERIFY_NOT_ALLOWED);
        }
        certificationNumberDao.removeCertificationNumber(email);
    }

    private boolean isVerify(String certificationNumber, String email) {
        return !(certificationNumberDao.hasKey(email) &&
                certificationNumberDao.getCertificationNumber(email)
                        .equals(certificationNumber));
    }
}

