package com.viksingh.keycloak.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRequestDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
