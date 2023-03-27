package com.shoekream.domain.user;

import com.shoekream.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorColumn
public abstract class UserBase extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(unique = true)
    protected String email;

    protected String password;

    @Enumerated(EnumType.STRING)
    protected UserRole userRole;

}
