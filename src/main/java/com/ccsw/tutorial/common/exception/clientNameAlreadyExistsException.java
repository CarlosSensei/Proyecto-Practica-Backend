package com.ccsw.tutorial.common.exception;

public class clientNameAlreadyExistsException extends IllegalArgumentException {

    public clientNameAlreadyExistsException() {
        super("Client name already exists");
    }
}
