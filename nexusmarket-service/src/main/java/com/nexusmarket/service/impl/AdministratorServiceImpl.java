package com.nexusmarket.service.impl;

import com.nexusmarket.domain.Address;
import com.nexusmarket.domain.Seller;
import com.nexusmarket.domain.Warehouse;
import com.nexusmarket.domain.enums.WarehouseType;
import com.nexusmarket.exception.DuplicateRegistrationException;
import com.nexusmarket.exception.ResourceNotFoundException;
import com.nexusmarket.repository.SellerRepository;
import com.nexusmarket.repository.UserRepository;
import com.nexusmarket.repository.WarehouseRepository;
import com.nexusmarket.service.AdministratorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdministratorServiceImpl implements AdministratorService {

    private final SellerRepository sellerRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    public AdministratorServiceImpl(SellerRepository sellerRepository,
                                     WarehouseRepository warehouseRepository,
                                     UserRepository userRepository) {
        this.sellerRepository = sellerRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Seller registerSeller(String fullName, String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateRegistrationException("A user is already registered with the email " + email);
        }
        Seller seller = new Seller(fullName, email);
        return sellerRepository.save(seller);
    }

    @Override
    @Transactional
    public Warehouse registerWarehouse(String name, WarehouseType warehouseType, Address location, Long sellerId) {
        Seller seller = null;
        if (warehouseType == WarehouseType.SELLER) {
            if (sellerId == null) {
                throw new IllegalArgumentException("A SELLER-type warehouse requires an owning seller");
            }
            seller = sellerRepository.findById(sellerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Seller not found: " + sellerId));
        }
        Warehouse warehouse = new Warehouse(name, warehouseType, location, seller);
        return warehouseRepository.save(warehouse);
    }
}
