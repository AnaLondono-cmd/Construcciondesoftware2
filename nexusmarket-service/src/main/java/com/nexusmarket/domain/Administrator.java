package com.nexusmarket.domain;

import com.nexusmarket.domain.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "administrators")
@Getter
@Setter
@NoArgsConstructor
public class Administrator extends User {

    public Administrator(String fullName, String email) {
        super(fullName, email, UserRole.ADMINISTRATOR);
    }
}
