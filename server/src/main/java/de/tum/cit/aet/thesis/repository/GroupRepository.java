package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    Optional<Group> findBySlug(String slug);
    boolean existsBySlug(String slug);
}