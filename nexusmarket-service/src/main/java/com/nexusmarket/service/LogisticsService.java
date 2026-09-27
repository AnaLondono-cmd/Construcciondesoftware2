package com.nexusmarket.service;

import com.nexusmarket.domain.Shipment;
import com.nexusmarket.domain.enums.ShipmentStatus;

public interface LogisticsService {

    Shipment registerDispatch(Long orderId, Long logisticsOperatorId, Long dispatchWarehouseId);

    Shipment updateShipmentStatus(Long shipmentId, ShipmentStatus newStatus);

    Shipment confirmDelivery(Long shipmentId);

    Shipment getShipment(Long shipmentId);
}
