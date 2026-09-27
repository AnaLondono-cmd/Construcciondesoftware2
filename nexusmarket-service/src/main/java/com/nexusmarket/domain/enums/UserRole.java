package com.nexusmarket.domain.enums;

/** A user's single, immutable role within NexusMarket (business rule: one role per user). */
public enum UserRole {
    BUYER,
    SELLER,
    LOGISTICS_OPERATOR,
    ADMINISTRATOR,
    SUPERVISOR
}
