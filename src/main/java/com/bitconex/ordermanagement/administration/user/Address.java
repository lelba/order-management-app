package com.bitconex.ordermanagement.administration.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "T_ADDRESS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(generator = "address_gen", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "address_gen", sequenceName = "address_seq", allocationSize = 1)
    private Long addressId;
    private String street;
    private Long houseNumber;
    private String place;
    private String country;
    @JsonIgnore
    @OneToOne(mappedBy = "address")
    private User user;
}

