package com.nexusmarket.service;

import com.nexusmarket.domain.Address;
import com.nexusmarket.domain.Warehouse;
import com.nexusmarket.domain.Seller;
import com.nexusmarket.domain.enums.WarehouseType;


public interface AdministratorService {

    Seller registerSeller(String fullName, String email);

    Warehouse registerWarehouse(String name, WarehouseType warehouseType, Address location, Long sellerId);
}
