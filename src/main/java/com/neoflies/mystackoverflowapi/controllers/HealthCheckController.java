package com.neoflies.mystackoverflowapi.controllers;

import com.neoflies.mystackoverflowapi.domains.Health;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {
  @GetMapping
  public ResponseEntity<Health> healthCheck() {
    Health health = new Health();
    health.setStatus(HttpStatus.OK.value());
    return new ResponseEntity<>(health, HttpStatus.OK);
  }
}
