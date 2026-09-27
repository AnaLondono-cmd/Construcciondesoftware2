package com.nexusmarket.domain;

import com.nexusmarket.domain.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "sellers")
@Getter
@Setter
@NoArgsConstructor
public class Seller extends User {

    @OneToMany(mappedBy = "owner")
    private List<Warehouse> ownedWarehouses = new ArrayList<>();

    @OneToMany(mappedBy = "seller")
    private List<Product> publishedProducts = new ArrayList<>();

    public Seller(String fullName, String email) {
        super(fullName, email, UserRole.SELLER);
    }
}
