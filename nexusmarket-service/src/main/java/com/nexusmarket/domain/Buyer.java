package com.nexusmarket.domain;

import com.nexusmarket.domain.enums.CommercialStatus;
import com.nexusmarket.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Buyer Management domain. A buyer never manages another buyer's information nor any inventory. */
@Entity
@Table(name = "buyers")
@Getter
@Setter
@NoArgsConstructor
public class Buyer extends User {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "primary_address_street")),
            @AttributeOverride(name = "city", column = @Column(name = "primary_address_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "primary_address_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "primary_address_country"))
    })
    private Address primaryAddress;

    @ElementCollection
    @CollectionTable(name = "additional_addresses", joinColumns = @JoinColumn(name = "buyer_id"))
    private List<Address> additionalAddresses = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private CommercialStatus commercialStatus = CommercialStatus.ENABLED;

    public Buyer(String fullName, String email, Address primaryAddress) {
        super(fullName, email, UserRole.BUYER);
        this.primaryAddress = primaryAddress;
        this.commercialStatus = CommercialStatus.ENABLED;
    }

    public void addAddress(Address address) {
        this.additionalAddresses.add(address);
    }

    public boolean canPurchase() {
        return isActive() && commercialStatus == CommercialStatus.ENABLED;
    }
}
