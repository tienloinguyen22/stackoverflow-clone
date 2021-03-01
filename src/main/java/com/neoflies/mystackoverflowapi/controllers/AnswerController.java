package com.neoflies.mystackoverflowapi.controllers;

import com.neoflies.mystackoverflowapi.domains.*;
import com.neoflies.mystackoverflowapi.dtos.CreateAnswerPayload;
import com.neoflies.mystackoverflowapi.dtos.FindResult;
import com.neoflies.mystackoverflowapi.exceptions.BadRequestException;
import com.neoflies.mystackoverflowapi.exceptions.ResourceNotFoundException;
import com.neoflies.mystackoverflowapi.repositories.AnswerRepository;
import com.neoflies.mystackoverflowapi.repositories.AnswerVoteRepository;
import com.neoflies.mystackoverflowapi.repositories.QuestionRepository;
import com.neoflies.mystackoverflowapi.utils.AuthorizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.neoflies.mystackoverflowapi.controllers.QuestionController.DOWN_VOTE;
import static com.neoflies.mystackoverflowapi.controllers.QuestionController.UP_VOTE;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {
  @PersistenceContext
  EntityManager entityManager;

  @Autowired
  AnswerRepository answerRepository;

  @Autowired
  QuestionRepository questionRepository;

  @Autowired
  AnswerVoteRepository answerVoteRepository;

  @Autowired
  AuthorizationUtils authorizationUtils;

  @GetMapping
  public ResponseEntity<FindResult<Answer>> findAnswers(
    @RequestParam(name = "question", required = true) String question,
    @RequestParam(name = "page", defaultValue = "1") Integer page,
    @RequestParam(name = "limit", defaultValue = "6") Integer limit,
    @RequestParam(name = "sortBy", defaultValue = "count") String sortBy,
    @RequestParam(name = "order", defaultValue = "desc") String order
  ) {
    Optional<User> currentUser = this.authorizationUtils.getCurrentUser();

    String dataQuery = String.format("SELECT * FROM answers WHERE question_id = '%s' ORDER BY %s %s OFFSET %d LIMIT %d;", question, sortBy, order, (page - 1) * limit, limit);
    String countQuery = String.format("SELECT count(*) FROM answers WHERE question_id = '%s';", question);

    int total = ((Number) this.entityManager.createNativeQuery(countQuery).getSingleResult()).intValue();
    List<Answer> answers = this.entityManager.createNativeQuery(dataQuery, Answer.class).getResultList();
    if (currentUser.isPresent()) {
      for (Answer answer : answers) {
        Optional<AnswerVote> optionalAnswerVote = this.answerVoteRepository.findByUserAndAnswer(currentUser.get(), answer);
        if (optionalAnswerVote.isPresent()) {
          answer.setVote(optionalAnswerVote.get().getVote());
        }
      }
    }

    FindResult<Answer> findResult = new FindResult<>(answers, total);
    return new ResponseEntity<>(findResult, HttpStatus.OK);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('CREATE_ANSWER')")
  public ResponseEntity<Answer> createAnswer(@Valid @RequestBody CreateAnswerPayload body) {
    Question question = this.questionRepository.findById(UUID.fromString(body.getQuestion())).orElseThrow(() -> new ResourceNotFoundException("answers/question-not-found", "Question not found"));
    Answer answer = new Answer();
    answer.setId(UUID.randomUUID());
    answer.setBody(body.getBody());
    answer.setQuestion(question);
    Answer result = this.answerRepository.save(answer);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PatchMapping("/{id}/up-vote")
  @PreAuthorize("hasAuthority('VOTE_ANSWER')")
  @Transactional
  public ResponseEntity<Answer> upVoteAnswer(@PathVariable String id) {
    Answer existedAnswer = this.answerRepository.findById(UUID.fromString(id))
      .orElseThrow(() -> new ResourceNotFoundException("up-vote-answer/answer-not-found", "Answer not found"));

    User currentUser = this.authorizationUtils.getCurrentUser().get();

    Optional<AnswerVote> optionalAnswerVote = this.answerVoteRepository.findByUserAndAnswer(currentUser, existedAnswer);
    Integer vote = UP_VOTE;
    AnswerVote answerVoteInfo;
    if (optionalAnswerVote.isPresent()) {
      AnswerVote existedAnswerVote = optionalAnswerVote.get();
      answerVoteInfo = existedAnswerVote;
      if (existedAnswerVote.getVote() == UP_VOTE) {
        throw new BadRequestException("up-vote-answer/already-up-vote-answer", "You already up vote this answer");
      } else {
        vote = 2;
        existedAnswerVote.setVote(UP_VOTE);
        this.answerVoteRepository.save(existedAnswerVote);
      }
    } else {
      vote = 1;
      AnswerVote answerVote = new AnswerVote();
      answerVote.setId(UUID.randomUUID());
      answerVote.setAnswer(existedAnswer);
      answerVote.setUser(currentUser);
      answerVote.setVote(1);
      answerVoteInfo = this.answerVoteRepository.save(answerVote);
    }

    existedAnswer.setVotes(existedAnswer.getVotes() + vote);
    Answer newAnswerInfo = this.answerRepository.save(existedAnswer);
    newAnswerInfo.setVote(answerVoteInfo.getVote());
    return new ResponseEntity<>(newAnswerInfo, HttpStatus.OK);
  }

  @PatchMapping("/{id}/down-vote")
  @PreAuthorize("hasAuthority('VOTE_ANSWER')")
  @Transactional
  public ResponseEntity<Answer> downVoteAnswer(@PathVariable String id) {
    Answer existedAnswer = this.answerRepository.findById(UUID.fromString(id))
      .orElseThrow(() -> new ResourceNotFoundException("down-vote-answer/answer-not-found", "Answer not found"));

    User currentUser = this.authorizationUtils.getCurrentUser().get();

    Optional<AnswerVote> optionalAnswerVote = this.answerVoteRepository.findByUserAndAnswer(currentUser, existedAnswer);
    Integer vote = DOWN_VOTE;
    AnswerVote answerVoteInfo;
    if (optionalAnswerVote.isPresent()) {
      AnswerVote existedAnswerVote = optionalAnswerVote.get();
      answerVoteInfo = existedAnswerVote;
      if (existedAnswerVote.getVote() == DOWN_VOTE) {
        throw new BadRequestException("down-vote-answer/already-down-vote-answer", "You already down vote this answer");
      } else {
        vote = -2;
        existedAnswerVote.setVote(DOWN_VOTE);
        this.answerVoteRepository.save(existedAnswerVote);
      }
    } else {
      vote = -1;
      AnswerVote answerVote = new AnswerVote();
      answerVote.setId(UUID.randomUUID());
      answerVote.setAnswer(existedAnswer);
      answerVote.setUser(currentUser);
      answerVote.setVote(-1);
      answerVoteInfo = this.answerVoteRepository.save(answerVote);
    }

    existedAnswer.setVotes(existedAnswer.getVotes() + vote);
    Answer newAnswerInfo = this.answerRepository.save(existedAnswer);
    newAnswerInfo.setVote(answerVoteInfo.getVote());
    return new ResponseEntity<>(newAnswerInfo, HttpStatus.OK);
  }
}
