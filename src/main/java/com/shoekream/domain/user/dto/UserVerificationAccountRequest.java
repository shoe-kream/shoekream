package com.shoekream.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class UserVerificationAccountRequest {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;
    @NotBlank(message = "인증번호는 필수 입력 항목입니다.")
    private String certificationNumber;

}
