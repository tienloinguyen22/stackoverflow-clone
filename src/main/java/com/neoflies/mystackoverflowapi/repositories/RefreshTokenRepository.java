package com.neoflies.mystackoverflowapi.repositories;

import com.neoflies.mystackoverflowapi.domains.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
}
