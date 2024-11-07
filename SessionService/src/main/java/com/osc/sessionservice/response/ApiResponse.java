package com.osc.sessionservice.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ApiResponse {

    private int statusCode;
    private Object dataObject;


}
