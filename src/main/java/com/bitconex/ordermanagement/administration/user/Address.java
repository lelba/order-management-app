package com.bitconex.ordermanagement.administration.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "\"T_ADDRESS\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @SequenceGenerator(name = "address_seq", sequenceName = "address_seq", allocationSize = 1)
    @GeneratedValue(generator = "address_seq", strategy = GenerationType.SEQUENCE)
    private Long addressId;
    private String street;
    private Long houseNumber;
    private String place;
    private String country;
    @JsonIgnore
    @OneToOne(mappedBy = "address")
    private User user;
}

