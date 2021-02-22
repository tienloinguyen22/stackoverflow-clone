package com.neoflies.mystackoverflowapi.controllers;

import com.neoflies.mystackoverflowapi.domains.Answer;
import com.neoflies.mystackoverflowapi.domains.Question;
import com.neoflies.mystackoverflowapi.dtos.CreateAnswerPayload;
import com.neoflies.mystackoverflowapi.exceptions.ResourceNotFoundException;
import com.neoflies.mystackoverflowapi.repositories.AnswerRepository;
import com.neoflies.mystackoverflowapi.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {
  @Autowired
  AnswerRepository answerRepository;

  @Autowired
  QuestionRepository questionRepository;

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
}
