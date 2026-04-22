package com.projetLLD.V1.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
