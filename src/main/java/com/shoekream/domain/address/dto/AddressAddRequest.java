package com.shoekream.domain.address.dto;

import com.shoekream.domain.address.Address;
import com.shoekream.domain.user.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AddressAddRequest {
    @NotBlank(message = "주소 이름은 필수 입력 항목입니다.")
    private String addressName;
    @NotBlank(message = "도로명 주소는 필수 입력 항목입니다.")
    private String roadNameAddress;
    @NotBlank(message = "상세 주소는 필수 입력 항목입니다.")
    private String detailedAddress;
    @NotBlank(message = "우편 번호는 필수 입력 항목입니다.")
    private String postalCode;

    public Address toEntity(User user) {
        return Address.builder()
                .addressName(this.addressName)
                .roadNameAddress(this.roadNameAddress)
                .detailedAddress(this.detailedAddress)
                .postalCode(this.postalCode)
                .user(user)
                .build();
    }
}
