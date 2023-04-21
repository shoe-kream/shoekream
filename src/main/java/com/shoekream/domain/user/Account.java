package com.shoekream.domain.user;

import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * 물건 판매 후 정산 받을 계좌 정보 은행명,예금주
 */

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Account {

    private String bankName;
    private String accountNumber;
    private String depositor;
}
