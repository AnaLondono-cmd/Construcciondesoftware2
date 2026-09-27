package com.nexusmarket.service;

import com.nexusmarket.domain.Address;
import com.nexusmarket.domain.Warehouse;
import com.nexusmarket.domain.Seller;
import com.nexusmarket.domain.enums.WarehouseType;

/**
 * Application service for the Seller Management and Warehouse Management domains.
 * Business rule: sellers cannot self-register; they are onboarded exclusively by an Administrator
 * (Business Flow step 1: Onboarding).
 */
public interface AdministratorService {

    /** Use case: seller onboarding (Business Flow step 1). */
    Seller registerSeller(String fullName, String email);

    /** Use case: registration of a seller's or the marketplace's first warehouse. */
    Warehouse registerWarehouse(String name, WarehouseType warehouseType, Address location, Long sellerId);
}
