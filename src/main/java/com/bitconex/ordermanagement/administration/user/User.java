package com.bitconex.ordermanagement.administration.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity(name = "User")
@Table(name = "T_USER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    @Id
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
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
    @JsonIgnore
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

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(
            name = "DOB"
    )
    private Date dateOfBirth;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

}
