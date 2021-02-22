package com.neoflies.mystackoverflowapi.controllers;

import com.neoflies.mystackoverflowapi.domains.Tag;
import com.neoflies.mystackoverflowapi.dtos.CreateTagPayload;
import com.neoflies.mystackoverflowapi.exceptions.BadRequestException;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/tags")
public class TagController {
  @Autowired
  TagRepository tagRepository;

  @PostMapping
  @PreAuthorize("hasAuthority('CREATE_TAG')")
  public ResponseEntity<Tag> createTag(@Valid @RequestBody CreateTagPayload body) {
    Boolean tagNameExists = this.tagRepository.existsByName(body.getName());
    if (tagNameExists) {
      throw new BadRequestException("tags/tag-name-exists", "Tag name already exists");
    }

    Tag tag = new Tag();
    tag.setId(UUID.randomUUID());
    tag.setName(body.getName());
    tag.setDescription(body.getDescription());

    Tag result = this.tagRepository.save(tag);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
