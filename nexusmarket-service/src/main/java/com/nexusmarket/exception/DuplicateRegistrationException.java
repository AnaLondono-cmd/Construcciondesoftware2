package com.nexusmarket.exception;

/** Thrown when an email or identifier that must be unique is already registered. */
public class DuplicateRegistrationException extends NexusMarketException {
    public DuplicateRegistrationException(String message) {
        super(message);
    }
}
