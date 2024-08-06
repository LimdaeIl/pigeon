package com.house.pigeon.role.repository;

import com.house.pigeon.role.model.Role;
import com.house.pigeon.role.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Boolean existsByRoleType(RoleType roleType);
    Optional<Role> findByRoleType(RoleType roleType);
}
