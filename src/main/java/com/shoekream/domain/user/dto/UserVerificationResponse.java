package com.shoekream.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserVerificationResponse {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;
    @NotBlank(message = "인증번호는 필수 입력 항목입니다.")
    private String certificationNumber;

    public UserVerificationResponse(UserVerificationResponse userVerificationResponse) {
        this.email = userVerificationResponse.getEmail();
        this.certificationNumber = userVerificationResponse.getCertificationNumber();
    }
}
