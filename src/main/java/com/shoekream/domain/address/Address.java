package com.shoekream.domain.address;

import com.shoekream.domain.BaseTimeEntity;
import com.shoekream.domain.address.dto.AddressResponse;
import com.shoekream.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Address extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String addressName;
    private String roadNameAddress;
    private String detailedAddress;
    private String postalCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    public AddressResponse toAddressResponse() {
        return AddressResponse.builder()
                .addressId(this.id)
                .address(String.format("%s %s %s", this.addressName, this.roadNameAddress, this.detailedAddress))
                .build();
    }
}