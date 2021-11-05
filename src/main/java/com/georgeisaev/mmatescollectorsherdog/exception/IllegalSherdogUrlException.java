package com.georgeisaev.mmatescollectorsherdog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class IllegalSherdogUrlException extends RuntimeException {

    public IllegalSherdogUrlException(String message) {
        super(message);
    }

}
