package com.osc.sessionservice.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CredentialDTO {

    @NotNull(message = "Please Enter Valid Details")
    private String userId;

    @NotNull(message = "Please Enter Valid Details")
    private String password;

    @NotNull(message = "Please Enter Valid Details")
    private String deviceName;
}
