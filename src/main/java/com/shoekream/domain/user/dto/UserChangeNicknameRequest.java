package com.shoekream.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class UserChangeNicknameRequest {

    @NotBlank(message = "변경 할 닉네임은 필수 입력 항목입니다.")
    private String nickName;
}
