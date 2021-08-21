package com.example.homework.customexceptions;

public class InvalidAPIProviderException extends Exception {
    public InvalidAPIProviderException(String provider) {
        super("No such provider: " + provider);
    }
}
