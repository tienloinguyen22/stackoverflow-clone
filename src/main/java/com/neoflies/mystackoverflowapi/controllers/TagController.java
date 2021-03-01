package com.neoflies.mystackoverflowapi.controllers;

import com.neoflies.mystackoverflowapi.domains.Tag;
import com.neoflies.mystackoverflowapi.dtos.CreateTagPayload;
import com.neoflies.mystackoverflowapi.dtos.FindResult;
import com.neoflies.mystackoverflowapi.exceptions.BadRequestException;
import com.neoflies.mystackoverflowapi.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tags")
public class TagController {
  @PersistenceContext
  EntityManager entityManager;

  @Autowired
  TagRepository tagRepository;

  @GetMapping
  public ResponseEntity<FindResult<Tag>> findTags(
    @RequestParam(name = "search", required = false) String search,
    @RequestParam(name = "page", defaultValue = "1") Integer page,
    @RequestParam(name = "limit", defaultValue = "6") Integer limit,
    @RequestParam(name = "sortBy", defaultValue = "count") String sortBy,
    @RequestParam(name = "order", defaultValue = "desc") String order
  ) {
    String dataQuery = "SELECT * FROM tags";
    String countQuery = "SELECT count(*) FROM tags";
    if (search != null && !search.isBlank()) {
      String condition = String.format(" WHERE to_tsvector(name) @@ to_tsquery('%s')", search);
      dataQuery += condition;
      countQuery += condition;

    }
    dataQuery += String.format(" ORDER BY %s %s OFFSET %d LIMIT %d;", sortBy, order, (page - 1) * limit, limit);
    countQuery += ";";

    List<Tag> tags = this.entityManager.createNativeQuery(dataQuery, Tag.class).getResultList();
    int total = ((Number) this.entityManager.createNativeQuery(countQuery).getSingleResult()).intValue();
    FindResult<Tag> findResult = new FindResult<>(tags, total);
    return new ResponseEntity<>(findResult, HttpStatus.OK);
  }


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
