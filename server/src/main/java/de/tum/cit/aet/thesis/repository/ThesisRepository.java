package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.Thesis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThesisRepository extends JpaRepository<Thesis, UUID> {
    Page<Thesis> findByGroupId(UUID groupId, Pageable pageable);
    Optional<Thesis> findByGroupIdAndId(UUID groupId, UUID id);
    boolean existsByGroupIdAndId(UUID groupId, UUID id);
    void deleteByGroupId(UUID groupId);
}