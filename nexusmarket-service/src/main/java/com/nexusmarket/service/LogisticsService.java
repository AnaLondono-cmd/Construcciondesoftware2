package com.nexusmarket.service;

import com.nexusmarket.domain.Shipment;
import com.nexusmarket.domain.enums.ShipmentStatus;

/** Application service for the Shipment Management (Logistics) domain. Applies only to physical products. */
public interface LogisticsService {

    /** Use case: packing, dispatch and transport of the order (Business Flow step 7). */
    Shipment registerDispatch(Long orderId, Long logisticsOperatorId, Long dispatchWarehouseId);

    Shipment updateShipmentStatus(Long shipmentId, ShipmentStatus newStatus);

    /** Use case: closing the order after delivery is confirmed (Business Flow step 8). */
    Shipment confirmDelivery(Long shipmentId);

    Shipment getShipment(Long shipmentId);
}
