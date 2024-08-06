package com.house.pigeon.role;

import com.house.pigeon.role.model.Role;
import com.house.pigeon.role.model.RoleType;
import com.house.pigeon.role.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RoleInitializer {

    private final RoleRepository roleRepository;

    @PostConstruct
    @Transactional
    public void initRoles() {
        for (RoleType roleType : RoleType.values()) {
            if (!roleRepository.existsByRoleType(roleType)) {
                roleRepository.save(Role.builder()
                        .roleType(roleType)
                        .build());
            }
        }
    }
}
