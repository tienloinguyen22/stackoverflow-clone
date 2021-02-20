package com.neoflies.mystackoverflowapi.repositories;

import com.neoflies.mystackoverflowapi.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String email);

  Boolean existsByEmail(String email);
}
