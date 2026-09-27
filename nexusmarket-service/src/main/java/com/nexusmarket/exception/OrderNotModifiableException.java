package com.nexusmarket.exception;

/** Thrown when trying to modify a completed or cancelled Order. */
public class OrderNotModifiableException extends NexusMarketException {
    public OrderNotModifiableException(String message) {
        super(message);
    }
}
