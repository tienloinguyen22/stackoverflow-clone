package com.neoflies.mystackoverflowapi.repositories;

import com.neoflies.mystackoverflowapi.domains.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
