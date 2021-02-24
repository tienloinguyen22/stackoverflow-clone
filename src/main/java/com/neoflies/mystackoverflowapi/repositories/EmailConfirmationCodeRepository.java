package com.neoflies.mystackoverflowapi.repositories;

import com.neoflies.mystackoverflowapi.domains.EmailConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailConfirmationCodeRepository extends JpaRepository<EmailConfirmationCode, UUID> {
}
