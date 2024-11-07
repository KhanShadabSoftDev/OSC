package com.osc.sessionservice.service.login;


import com.osc.sessionservice.dto.CredentialDTO;
import com.osc.sessionservice.dto.LoginResponseDto;

public interface LoginService {

    LoginResponseDto createSession(CredentialDTO credentialDTO);

}
