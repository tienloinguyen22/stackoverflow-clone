package com.neoflies.mystackoverflowapi.repositories;

import com.neoflies.mystackoverflowapi.domains.Question;
import com.neoflies.mystackoverflowapi.domains.QuestionVote;
import com.neoflies.mystackoverflowapi.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface QuestionVoteRepository extends JpaRepository<QuestionVote, UUID> {
  Optional<QuestionVote> findByUserAndQuestion(User user, Question questionId);
}
