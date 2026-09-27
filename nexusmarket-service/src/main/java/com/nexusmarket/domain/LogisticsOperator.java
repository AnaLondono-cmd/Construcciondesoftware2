package com.nexusmarket.domain;

import com.nexusmarket.domain.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Responsible for the physical operation of warehouses and dispatches. */
@Entity
@Table(name = "logistics_operators")
@Getter
@Setter
@NoArgsConstructor
public class LogisticsOperator extends User {

    public LogisticsOperator(String fullName, String email) {
        super(fullName, email, UserRole.LOGISTICS_OPERATOR);
    }
}
