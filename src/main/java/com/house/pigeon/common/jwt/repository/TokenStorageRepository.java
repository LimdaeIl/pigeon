package com.house.pigeon.common.jwt.repository;

import com.house.pigeon.common.jwt.model.TokenStorage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenStorageRepository extends JpaRepository<TokenStorage, Long> {

    Optional<TokenStorage> findByToken(String token);
}
