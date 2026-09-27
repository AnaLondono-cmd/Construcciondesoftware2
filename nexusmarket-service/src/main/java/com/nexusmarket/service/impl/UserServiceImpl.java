package com.nexusmarket.service.impl;

import com.nexusmarket.domain.Address;
import com.nexusmarket.domain.Buyer;
import com.nexusmarket.domain.User;
import com.nexusmarket.domain.enums.UserStatus;
import com.nexusmarket.exception.DuplicateRegistrationException;
import com.nexusmarket.exception.ResourceNotFoundException;
import com.nexusmarket.repository.BuyerRepository;
import com.nexusmarket.repository.UserRepository;
import com.nexusmarket.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;

    public UserServiceImpl(UserRepository userRepository, BuyerRepository buyerRepository) {
        this.userRepository = userRepository;
        this.buyerRepository = buyerRepository;
    }

    @Override
    @Transactional
    public Buyer registerBuyer(String fullName, String email, Address primaryAddress) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateRegistrationException("A user is already registered with the email " + email);
        }
        Buyer buyer = new Buyer(fullName, email, primaryAddress);
        return buyerRepository.save(buyer);
    }

    @Override
    @Transactional
    public void blockUser(Long userId) {
        User user = getUser(userId);
        user.changeStatus(UserStatus.BLOCKED);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void activateUser(Long userId) {
        User user = getUser(userId);
        user.changeStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }
}
