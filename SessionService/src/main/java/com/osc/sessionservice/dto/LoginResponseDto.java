package com.osc.sessionservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponseDto {

    @NotNull(message = "Please Enter Valid Details")
    private String sessionId;

    @NotNull(message = "Please Enter Valid Details")
    private String name;

}
