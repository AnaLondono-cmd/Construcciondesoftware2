package com.nexusmarket.exception;

/** Thrown when a reservation/adjustment would violate the non-negative stock invariant. */
public class InsufficientStockException extends NexusMarketException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
