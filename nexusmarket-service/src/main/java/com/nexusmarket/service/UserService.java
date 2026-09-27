package com.nexusmarket.service;

import com.nexusmarket.domain.Address;
import com.nexusmarket.domain.Buyer;
import com.nexusmarket.domain.User;


public interface UserService {

    Buyer registerBuyer(String fullName, String email, Address primaryAddress);

    void blockUser(Long userId);

    void activateUser(Long userId);

    User getUser(Long userId);
}
