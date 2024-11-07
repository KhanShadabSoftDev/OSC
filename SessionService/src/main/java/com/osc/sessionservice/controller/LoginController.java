package com.osc.sessionservice.controller;


import com.osc.sessionservice.dto.CredentialDTO;
import com.osc.sessionservice.dto.LoginResponseDto;
import com.osc.sessionservice.response.ApiResponse;
import com.osc.sessionservice.service.login.LoginService;
import com.osc.sessionservice.response.StatusCode;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final LoginService loginService;

    private ApiResponse apiResponse;

    public LoginController(LoginService loginService, ApiResponse apiResponse) {
        this.loginService = loginService;
        this.apiResponse = apiResponse;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody CredentialDTO credentialDTO) {

        LoginResponseDto sessionCreated = loginService.createSession(credentialDTO);

        if (sessionCreated != null) {

            apiResponse = new ApiResponse(StatusCode.LOGIN_SUCCESS, sessionCreated);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }
        apiResponse = new ApiResponse(StatusCode.LOGIN_FAILURE, null);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }


}
