package com.bitconex.ordermanagement.administration.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {

    private Long addressId;
    private String street;
    private Long houseNumber;
    private String place;
    private String country;

}
