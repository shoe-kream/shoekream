package com.shoekream.domain.user.dto;

import com.shoekream.domain.address.Address;
import com.shoekream.domain.user.Account;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UserInfoForTrade {

    private Account account;
    private List<Address> addressList = new ArrayList<>();
}