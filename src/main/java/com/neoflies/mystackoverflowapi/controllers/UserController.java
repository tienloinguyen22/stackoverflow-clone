package com.neoflies.mystackoverflowapi.controllers;

import com.neoflies.mystackoverflowapi.domains.Tag;
import com.neoflies.mystackoverflowapi.domains.User;
import com.neoflies.mystackoverflowapi.dtos.FindResult;
import com.neoflies.mystackoverflowapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
  @PersistenceContext
  EntityManager entityManager;

  @Autowired
  UserRepository userRepository;

  @GetMapping
  public ResponseEntity<FindResult<User>> findUsers(
    @RequestParam(name = "search", required = false) String search,
    @RequestParam(name = "page", defaultValue = "1") Integer page,
    @RequestParam(name = "limit", defaultValue = "6") Integer limit,
    @RequestParam(name = "sortBy", defaultValue = "count") String sortBy,
    @RequestParam(name = "order", defaultValue = "desc") String order
  ) {
    String dataQuery = "SELECT * FROM users";
    String countQuery = "SELECT count(*) FROM users";
    if (search != null && !search.isBlank()) {
      String condition = String.format(" WHERE to_tsvector(email) @@ to_tsquery('%s') OR to_tsvector(firstName) @@ to_tsquery('%s') OR to_tsvector(lastName) @@ to_tsquery('%s')", search, search, search);
      dataQuery += condition;
      countQuery += condition;

    }
    dataQuery += String.format(" ORDER BY %s %s OFFSET %d LIMIT %d;", sortBy, order, (page - 1) * limit, limit);
    countQuery += ";";

    List<User> users = this.entityManager.createNativeQuery(dataQuery, User.class).getResultList();
    int total = ((Number) this.entityManager.createNativeQuery(countQuery).getSingleResult()).intValue();
    FindResult<User> findResult = new FindResult<>(users, total);
    return new ResponseEntity<>(findResult, HttpStatus.OK);
  }
}
