package com.neoflies.mystackoverflowapi.controllers;

import com.neoflies.mystackoverflowapi.domains.Question;
import com.neoflies.mystackoverflowapi.domains.Tag;
import com.neoflies.mystackoverflowapi.dtos.CreateQuestionPayload;
import com.neoflies.mystackoverflowapi.repositories.QuestionRepository;
import com.neoflies.mystackoverflowapi.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
  @Autowired
  QuestionRepository questionRepository;

  @Autowired
  TagRepository tagRepository;

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
    Question result = this.questionRepository.save(question);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
