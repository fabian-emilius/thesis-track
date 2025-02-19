package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.ResearchGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResearchGroupRepository extends JpaRepository<ResearchGroup, UUID> {
    Optional<ResearchGroup> findBySlug(String slug);
    
    boolean existsBySlug(String slug);
    
    @Query("SELECT g FROM ResearchGroup g LEFT JOIN FETCH g.members WHERE g.id = :id")
    Optional<ResearchGroup> findByIdWithMembers(UUID id);
    
    @Query("SELECT DISTINCT g FROM ResearchGroup g LEFT JOIN FETCH g.members m WHERE m.user.id = :userId")
    List<ResearchGroup> findAllByUserId(UUID userId);
}