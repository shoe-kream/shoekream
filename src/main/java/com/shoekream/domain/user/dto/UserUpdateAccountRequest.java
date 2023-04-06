package com.shoekream.domain.user.dto;

import com.shoekream.domain.user.Account;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class UserUpdateAccountRequest {

    @NotBlank(message = "은행명은 필수 입력 항목입니다.")
    private String bankName;
    @NotBlank(message = "계좌 번호는 필수 입력 항목입니다.")
    private String accountNumber;
    @NotBlank(message = "예금주는 필수 입력 항목입니다.")
    private String depositor;

    public Account toAccount() {
        return Account.builder()
                .bankName(this.bankName)
                .accountNumber(this.accountNumber)
                .depositor(this.depositor)
                .build();
    }
}
