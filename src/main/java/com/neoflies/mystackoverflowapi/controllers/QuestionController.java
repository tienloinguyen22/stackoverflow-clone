package com.neoflies.mystackoverflowapi.controllers;

import com.neoflies.mystackoverflowapi.domains.*;
import com.neoflies.mystackoverflowapi.dtos.CreateQuestionPayload;
import com.neoflies.mystackoverflowapi.dtos.FindResult;
import com.neoflies.mystackoverflowapi.exceptions.BadRequestException;
import com.neoflies.mystackoverflowapi.exceptions.ResourceNotFoundException;
import com.neoflies.mystackoverflowapi.repositories.QuestionRepository;
import com.neoflies.mystackoverflowapi.repositories.QuestionVoteRepository;
import com.neoflies.mystackoverflowapi.repositories.TagRepository;
import com.neoflies.mystackoverflowapi.repositories.UserRepository;
import com.neoflies.mystackoverflowapi.utils.SlugUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
  public static final Integer UP_VOTE = 1;
  public static final Integer DOWN_VOTE = -1;

  @PersistenceContext
  EntityManager entityManager;

  @Autowired
  QuestionRepository questionRepository;

  @Autowired
  TagRepository tagRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  QuestionVoteRepository questionVoteRepository;

  @GetMapping
  @PreAuthorize("hasAuthority('VIEW_QUESTION')")
  public ResponseEntity<FindResult<Question>> findQuestions(
    @RequestParam(name = "search", required = false) String search,
    @RequestParam(name = "page", defaultValue = "1") Integer page,
    @RequestParam(name = "limit", defaultValue = "6") Integer limit,
    @RequestParam(name = "sortBy", defaultValue = "count") String sortBy,
    @RequestParam(name = "order", defaultValue = "desc") String order
  ) {
    String dataQuery = "SELECT * FROM questions";
    String countQuery = "SELECT count(*) FROM questions";
    if (search != null && !search.isBlank()) {
      String condition = String.format(" WHERE to_tsvector(title) @@ to_tsquery('%s')", search);
      dataQuery += condition;
      countQuery += condition;

    }
    dataQuery += String.format(" ORDER BY %s %s OFFSET %d LIMIT %d;", sortBy, order, (page - 1) * limit, limit);
    countQuery += ";";

    List<Question> questions = this.entityManager.createNativeQuery(dataQuery, Question.class).getResultList();
    int total = ((Number) this.entityManager.createNativeQuery(countQuery).getSingleResult()).intValue();
    FindResult<Question> findResult = new FindResult<>(questions, total);
    return new ResponseEntity<>(findResult, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('VIEW_QUESTION')")
  public ResponseEntity<Question> findQuestionById(@PathVariable String id) {
    Question question = this.questionRepository.findById(UUID.fromString(id))
      .orElseThrow(() -> new ResourceNotFoundException("questions/question-not-found", "Question not found"));
    return new ResponseEntity<>(question, HttpStatus.OK);
  }


  @PostMapping
  @PreAuthorize("hasAuthority('CREATE_QUESTION')")
  public ResponseEntity<Question> createQuestion(@Valid @RequestBody CreateQuestionPayload body) {
    List<Tag> tags = this.tagRepository.findByNameIn(body.getTags());
    for (Tag tag : tags) {
      tag.setCount(tag.getCount() + 1);
      this.tagRepository.save(tag);
    }

    Question question = new Question();
    question.setId(UUID.randomUUID());
    question.setTitle(body.getTitle());
    question.setBody(body.getBody());
    question.setTags(tags);
    question.setSlug(SlugUtils.slugify(body.getTitle()));
    Question result = this.questionRepository.save(question);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PatchMapping("/{id}/up-vote")
  @PreAuthorize("hasAuthority('VOTE_QUESTION')")
  @Transactional
  public ResponseEntity<Question> upVoteQuestion(@PathVariable String id) {
    Question existedQuestion = this.questionRepository.findById(UUID.fromString(id))
      .orElseThrow(() -> new ResourceNotFoundException("up-vote-question/question-not-found", "Question not found"));

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    ApplicationUserDetails userDetails = (ApplicationUserDetails) authentication.getPrincipal();
    UUID currentUserId = userDetails.getId();
    User currentUser = this.userRepository.findById(currentUserId)
      .orElseThrow(() -> new ResourceNotFoundException("up-vote-question/user-not-found", "User not found"));

    Optional<QuestionVote> optionalQuestionVote = this.questionVoteRepository.findByUserAndQuestion(currentUser, existedQuestion);
    Integer vote = UP_VOTE;
    if (optionalQuestionVote.isPresent()) {
      QuestionVote existedQuestionVote = optionalQuestionVote.get();
      if (existedQuestionVote.getVote() == UP_VOTE) {
        throw new BadRequestException("up-vote-question/already-up-vote-question", "You already up vote this question");
      } else {
        vote = 2;
        existedQuestionVote.setVote(UP_VOTE);
        this.questionVoteRepository.save(existedQuestionVote);
      }
    } else {
      vote = 1;
      QuestionVote questionVote = new QuestionVote();
      questionVote.setId(UUID.randomUUID());
      questionVote.setQuestion(existedQuestion);
      questionVote.setUser(currentUser);
      questionVote.setVote(1);
      this.questionVoteRepository.save(questionVote);
    }

    existedQuestion.setVotes(existedQuestion.getVotes() + vote);
    Question newQuestionInfo = this.questionRepository.save(existedQuestion);
    return new ResponseEntity<>(newQuestionInfo, HttpStatus.OK);
  }

  @PatchMapping("/{id}/down-vote")
  @PreAuthorize("hasAuthority('VOTE_QUESTION')")
  @Transactional
  public ResponseEntity<Question> downVoteQuestion(@PathVariable String id) {
    Question existedQuestion = this.questionRepository.findById(UUID.fromString(id))
      .orElseThrow(() -> new ResourceNotFoundException("up-vote/question-not-found", "Question not found"));

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    ApplicationUserDetails userDetails = (ApplicationUserDetails) authentication.getPrincipal();
    UUID currentUserId = userDetails.getId();
    User currentUser = this.userRepository.findById(currentUserId)
      .orElseThrow(() -> new ResourceNotFoundException("up-vote/user-not-found", "User not found"));

    Optional<QuestionVote> optionalQuestionVote = this.questionVoteRepository.findByUserAndQuestion(currentUser, existedQuestion);
    Integer vote = DOWN_VOTE;
    if (optionalQuestionVote.isPresent()) {
      QuestionVote existedQuestionVote = optionalQuestionVote.get();
      if (existedQuestionVote.getVote() == DOWN_VOTE) {
        throw new BadRequestException("up-vote/already-down-vote-question", "You already down vote this question");
      } else {
        vote = -2;
        existedQuestionVote.setVote(DOWN_VOTE);
        this.questionVoteRepository.save(existedQuestionVote);
      }
    } else {
      vote = -1;
      QuestionVote questionVote = new QuestionVote();
      questionVote.setId(UUID.randomUUID());
      questionVote.setQuestion(existedQuestion);
      questionVote.setUser(currentUser);
      questionVote.setVote(-1);
      this.questionVoteRepository.save(questionVote);
    }

    existedQuestion.setVotes(existedQuestion.getVotes() + vote);
    Question newQuestionInfo = this.questionRepository.save(existedQuestion);
    return new ResponseEntity<>(newQuestionInfo, HttpStatus.OK);
  }
}
