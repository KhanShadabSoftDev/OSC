package com.osc.sessionservice.controller;


import com.osc.sessionservice.dto.LogoutDTO;
import com.osc.sessionservice.response.ApiResponse;
import com.osc.sessionservice.service.logout.LogoutService;
import com.osc.sessionservice.response.StatusCode;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutController {


    private ApiResponse apiResponse;

    private final LogoutService logoutService;

    public LogoutController(LogoutService logoutService, ApiResponse apiResponse) {
        this.logoutService = logoutService;
        this.apiResponse = apiResponse;
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@Valid @RequestBody LogoutDTO logoutDTO) {

        boolean sessionEnded = logoutService.LogoutSession(logoutDTO);

        if (sessionEnded) {
            apiResponse = new ApiResponse(StatusCode.LOGOUT_SUCCESS, null);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }
        apiResponse = new ApiResponse(StatusCode.LOGOUT_FAILURE, null);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
