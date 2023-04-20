package com.shoekream.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserFindPasswordResponse {

    private String email;
    private String tempPassword;

    public UserFindPasswordResponse(UserFindPasswordResponse userFindPasswordResponse) {
        this.email = userFindPasswordResponse.getEmail();
        this.tempPassword = userFindPasswordResponse.getTempPassword();
    }
}
