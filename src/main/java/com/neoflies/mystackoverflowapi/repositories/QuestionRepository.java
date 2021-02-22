package com.neoflies.mystackoverflowapi.repositories;

import com.neoflies.mystackoverflowapi.domains.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
}
