package com.shoekream.common.event;

import com.shoekream.domain.user.dto.UserCertificateResponse;
import com.shoekream.domain.user.dto.UserFindPasswordResponse;
import com.shoekream.service.EmailCertificationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailCertificationService emailCertificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = UserFindPasswordResponse.class)
    public void handle(UserFindPasswordResponse event) throws MessagingException {

        emailCertificationService.sendEmailForFindPassword(event.getEmail(), event.getTempPassword());
    }

    @TransactionalEventListener(classes = UserCertificateResponse.class)
    public void handle(UserCertificateResponse event) throws MessagingException {

        emailCertificationService.sendEmailForCertification(event.getEmail(), event.getCertificationNumber());
    }

}
