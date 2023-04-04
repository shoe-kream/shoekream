package com.shoekream.domain.user.dto;

import com.shoekream.domain.user.User;
import com.shoekream.domain.user.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
public class UserCreateRequest {

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",message = "올바른 이메일 형식이 아닙니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",message = "최소 8글자로 입력해주시고, 글자 1개, 숫자 1개, 특수문자 1개를 포함해주세요.")
    private String password;
    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    private String nickname;
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$",message = "000-0000-0000 형식으로 전화번호를 입력해주세요.")
    private String phone;

    public User toEntity(){
        return User.builder()
                .email(this.email)
                .password(this.password)
                .nickname(this.nickname)
                .phone(this.phone)
                .point(0L)
                .nicknameModifiedDate(LocalDateTime.now())
                .userRole(UserRole.ROLE_ANONYMOUS)
                .build();
    }

    public void encodePassword(BCryptPasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }

}
