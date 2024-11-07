package com.osc.sessionservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LogoutDTO {

    @NotNull(message = "Please Enter Valid Details")
    private String sessionId;

    @NotNull(message = "Please Enter Valid Details")
    private String userId;



}
