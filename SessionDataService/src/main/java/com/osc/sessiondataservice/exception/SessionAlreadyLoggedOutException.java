package com.osc.sessiondataservice.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SessionAlreadyLoggedOutException extends RuntimeException {
    public SessionAlreadyLoggedOutException(String msg) {
        super(msg);

    }
}
