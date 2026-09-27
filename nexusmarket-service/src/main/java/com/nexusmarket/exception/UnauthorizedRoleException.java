package com.nexusmarket.exception;

/** Business rule: no participant may manage information outside of its own role. */
public class UnauthorizedRoleException extends NexusMarketException {
    public UnauthorizedRoleException(String message) {
        super(message);
    }
}
