package com.shoekream.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserCertificateResponse {

    private String email;
    private String certificationNumber;

    public UserCertificateResponse(UserCertificateResponse userFindPasswordResponse) {
        this.email = userFindPasswordResponse.getEmail();
        this.certificationNumber = userFindPasswordResponse.getCertificationNumber();
    }
}
