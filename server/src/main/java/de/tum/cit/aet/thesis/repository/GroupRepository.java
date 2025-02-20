package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {
    Optional<Group> findBySlug(String slug);
    boolean existsBySlug(String slug);
}