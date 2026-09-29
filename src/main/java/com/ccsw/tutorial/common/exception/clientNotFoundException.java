package com.ccsw.tutorial.common.exception;

public class clientNotFoundException extends RuntimeException{

    public clientNotFoundException() {
        super("Could not find author with this id");
    }
}
