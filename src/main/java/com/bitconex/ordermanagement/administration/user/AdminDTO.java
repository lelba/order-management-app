package com.bitconex.ordermanagement.administration.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDTO {

    private Long id;
    private String username;
    private String email;
    private UserRole role;
}
