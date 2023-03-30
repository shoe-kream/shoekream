package com.shoekream.domain.user.dto;

import com.shoekream.domain.user.User;
import com.shoekream.domain.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserCreateRequest {
    private String email;
    private String password;
    private String nickname;
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
