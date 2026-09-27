package com.nexusmarket.config;

import com.nexusmarket.domain.*;
import com.nexusmarket.domain.enums.WarehouseType;
import com.nexusmarket.domain.enums.ProductType;
import com.nexusmarket.repository.LogisticsOperatorRepository;
import com.nexusmarket.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Walks through the "General Business Flow" (functional specification, section 6.1) end to end,
 * using only the application services, to demonstrate that use cases and business rules are
 * fully resolved by the service layer.
 *
 * Activated only with the "demo" profile: mvn spring-boot:run -Dspring-boot.run.profiles=demo
 */
@Component
@Profile("demo")
public class BusinessFlowDemoRunner implements CommandLineRunner {

    private final UserService userService;
    private final AdministratorService administratorService;
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final CartService cartService;
    private final OrderService orderService;
    private final LogisticsService logisticsService;
    private final ReturnService returnService;
    private final LogisticsOperatorRepository logisticsOperatorRepository;

    public BusinessFlowDemoRunner(UserService userService,
                                   AdministratorService administratorService,
                                   ProductService productService,
                                   InventoryService inventoryService,
                                   CartService cartService,
                                   OrderService orderService,
                                   LogisticsService logisticsService,
                                   ReturnService returnService,
                                   LogisticsOperatorRepository logisticsOperatorRepository) {
        this.userService = userService;
        this.administratorService = administratorService;
        this.productService = productService;
        this.inventoryService = inventoryService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.logisticsService = logisticsService;
        this.returnService = returnService;
        this.logisticsOperatorRepository = logisticsOperatorRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== DEMO: NexusMarket General Business Flow (section 6.1) ===");

        // 1. Onboarding: the Administrator registers the seller and its first warehouse.
        Seller seller = administratorService.registerSeller("Andean Crafts Ltd.", "contact@andeancrafts.com");
        Address warehouseAddress = new Address("Cra 45 #12-30", "Medellin", "050001", "Colombia");
        Warehouse warehouse = administratorService.registerWarehouse("Medellin Main Warehouse", WarehouseType.SELLER,
                warehouseAddress, seller.getId());
        System.out.println("1. Seller and warehouse registered: " + seller.getFullName() + " / " + warehouse.getName());

        // 2. Catalog: the seller registers products and defines their features.
        Product product = productService.registerProduct(seller.getId(),
                "Wool Poncho", "Handmade 100% sheep wool poncho", ProductType.PHYSICAL, new BigDecimal("120000"));
        productService.addVariant(product.getId(), "Size", "M", "PONCHO-M");
        System.out.println("2. Product registered: " + product.getName());

        // 3. Inventory: initial stock is registered in the associated warehouses.
        Inventory inventory = inventoryService.registerInitialStock(product.getId(), warehouse.getId(), 50);
        System.out.println("3. Initial stock registered: " + inventory.getAvailableQuantity() + " units");

        // 4. Publication: the products become visible in the public catalog.
        productService.publishProduct(product.getId());
        System.out.println("4. Product published in the catalog");

        // Buyer and logistics operator registration
        Buyer buyer = userService.registerBuyer("Laura Gomez", "laura.gomez@mail.com",
                new Address("Calle 10 #5-20", "Bogota", "110111", "Colombia"));
        LogisticsOperator operator = logisticsOperatorRepository.save(new LogisticsOperator("Carlos Ruiz", "carlos.ruiz@nexusmarket.com"));

        // 5. Purchase: the buyer selects products through the cart and confirms the order.
        cartService.addProduct(buyer.getId(), product.getId(), null, 2);
        Order order = orderService.confirmOrder(buyer.getId(), warehouse.getId());
        System.out.println("5. Order confirmed #" + order.getId() + " status=" + order.getStatus());

        // 6. Transaction: payment is validated and the fulfillment flow begins.
        orderService.confirmPayment(order.getId());
        System.out.println("6. Payment confirmed, invoice issued. status=" + order.getStatus());

        // 7. Logistics: packing, dispatch and transport of the order.
        Shipment shipment = logisticsService.registerDispatch(order.getId(), operator.getId(), warehouse.getId());
        System.out.println("7. Shipment dispatched, tracking number=" + shipment.getTrackingNumber());

        // 8. Closing: the order is marked as completed after delivery is confirmed.
        logisticsService.confirmDelivery(shipment.getId());
        System.out.println("8. Delivery confirmed. Final order status=" + orderService.getOrder(order.getId()).getStatus());

        // Additional use case: post-sale return and refund
        ReturnRequest returnRequest = returnService.requestReturn(order.getId(), "Wrong size");
        returnService.approve(returnRequest.getId(), warehouse.getId());
        returnService.generateRefund(returnRequest.getId());
        System.out.println("9. Return approved and refund generated");

        System.out.println("=== DEMO finished successfully ===");
    }
}
