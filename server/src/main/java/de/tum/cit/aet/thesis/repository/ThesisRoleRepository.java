package de.tum.cit.aet.thesis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import de.tum.cit.aet.thesis.entity.ThesisRole;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.key.ThesisRoleId;

import java.util.List;
import java.util.UUID;


@Repository
public interface ThesisRoleRepository extends JpaRepository<ThesisRole, ThesisRoleId> {
    /**
     * Delete all thesis roles for a thesis
     * @param thesisId The thesis ID
     * @return List of deleted thesis roles
     */
    List<ThesisRole> deleteByThesisId(UUID thesisId);
    
    /**
     * Find all thesis roles for a user
     * @param user The user 
     * @return List of thesis roles
     */
    List<ThesisRole> findByUser(User user);
}
