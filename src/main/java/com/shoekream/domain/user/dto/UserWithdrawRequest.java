package com.shoekream.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class UserWithdrawRequest {

    @NotBlank(message = "변경 전 비밀번호 필수 입력 항목입니다.")
    private String password;
}
