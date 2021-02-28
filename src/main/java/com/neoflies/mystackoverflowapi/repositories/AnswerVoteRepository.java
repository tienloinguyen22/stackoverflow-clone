package com.neoflies.mystackoverflowapi.repositories;

import com.neoflies.mystackoverflowapi.domains.Answer;
import com.neoflies.mystackoverflowapi.domains.AnswerVote;
import com.neoflies.mystackoverflowapi.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AnswerVoteRepository extends JpaRepository<AnswerVote, UUID> {
  Optional<AnswerVote> findByUserAndAnswer(User user, Answer answer);
}
