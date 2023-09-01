package com.bitconex.ordermanagement.administration.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity(name = "User")
@Table(name = "\"T_USER\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class User {

    @Id
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1, initialValue = 2)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @Column(
            name = "ID",
            updatable = false
    )
    private Long id;
    @Column(
            name = "USERNAME",
            nullable = false,
            unique = true
    )
    private String userName;

    @Column(
            name = "PASSWORD",
            nullable = false
    )
    private String password;
    @Column(
            name = "EMAIL",
            nullable = false
    )
    private String email;

    @Column(
            name = "ROLE",
            nullable = false
    )
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(
            name = "NAME"
    )
    private String name;

    @Column(
            name = "SURNAME"
    )
    private String surname;

    @Column(
            name = "DOB"
    )
    private Date dateOfBirth;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private Address address;
}
