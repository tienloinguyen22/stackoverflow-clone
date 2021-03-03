package com.neoflies.mystackoverflowapi.repositories;

import com.neoflies.mystackoverflowapi.domains.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
  List<Tag> findByNameIn(List<String> nameList);
  Optional<Tag> findByName(String name);
  Boolean existsByName(String name);
}
