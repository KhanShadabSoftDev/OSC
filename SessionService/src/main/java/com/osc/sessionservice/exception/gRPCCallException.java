package com.osc.sessionservice.exception;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class gRPCCallException extends RuntimeException {

    public gRPCCallException(String message) {
        super(message);
    }


}

