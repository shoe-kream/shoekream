package com.shoekream.domain.user.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
public class UserLoginRequest {
    private String email;
    private String password;
}
