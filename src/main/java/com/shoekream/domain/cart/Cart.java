package com.shoekream.domain.cart;

import com.shoekream.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", orphanRemoval = true)
    private Set<CartProduct> wishList = new HashSet<>();

    public void addCartProducts(CartProduct cartItem) {
        this.wishList.add(cartItem);
    }


}