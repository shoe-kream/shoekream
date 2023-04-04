package com.shoekream.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserChangePasswordRequest {
    @NotBlank(message = "변경 전 비밀번호 필수 입력 항목입니다.")
    private String oldPassword;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",message = "최소 8글자로 입력해주시고, 글자 1개, 숫자 1개, 특수문자 1개를 포함해주세요.")
    private String newPassword;
}
