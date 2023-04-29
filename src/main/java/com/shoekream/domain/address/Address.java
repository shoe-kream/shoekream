package com.shoekream.domain.address;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.BaseTimeEntity;
import com.shoekream.domain.address.dto.AddressRequest;
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

//    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    public AddressResponse toAddressResponse() {
        return AddressResponse.builder()
                .addressId(this.id)
                .address(String.format("%s %s %s", this.addressName, this.roadNameAddress, this.detailedAddress))
                .addressName(this.addressName)
                .build();
    }

    public void checkUser(User user) {
        if (!this.user.equals(user)) {
            throw new ShoeKreamException(ErrorCode.USER_NOT_MATCH);
        }
    }

    public void update(AddressRequest request) {
        this.addressName = request.getAddressName();
        this.roadNameAddress = request.getRoadNameAddress();
        this.detailedAddress = request.getDetailedAddress();
    }
}