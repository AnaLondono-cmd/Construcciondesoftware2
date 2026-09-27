package com.nexusmarket.exception;

/** Base exception for every business-rule violation in NexusMarket. */
public class NexusMarketException extends RuntimeException {
    public NexusMarketException(String message) {
        super(message);
    }
}
