package com.nexusmarket.service;

import com.nexusmarket.domain.Address;
import com.nexusmarket.domain.Buyer;
import com.nexusmarket.domain.User;

/**
 * Application service for the User Management domain.
 * Enforces the single-role-per-user rule and email uniqueness.
 */
public interface UserService {

    /** Use case: buyer registration (self-registration is allowed, unlike sellers). */
    Buyer registerBuyer(String fullName, String email, Address primaryAddress);

    void blockUser(Long userId);

    void activateUser(Long userId);

    User getUser(Long userId);
}
