package de.tum.cit.aet.thesis.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import de.tum.cit.aet.thesis.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUniversityId(String universityId);

    @Query(
            "SELECT DISTINCT u FROM User u LEFT JOIN UserGroup g ON (u.id = g.id.userId) WHERE " +
            "(:groups IS NULL OR g.id.group IN :groups) AND " +
            "(:searchQuery IS NULL OR LOWER(u.firstName) || ' ' || LOWER(u.lastName) LIKE %:searchQuery% OR " +
            "LOWER(u.email) LIKE %:searchQuery% OR " +
            "LOWER(u.matriculationNumber) LIKE %:searchQuery% OR " +
            "LOWER(u.universityId) LIKE %:searchQuery%)"
    )
    Page<User> searchUsers(@Param("searchQuery") String searchQuery, @Param("groups") Set<String> groups, Pageable page);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN UserGroup g ON (u.id = g.id.userId) WHERE g.id.group IN :roles")
    List<User> getRoleMembers(@Param("roles") Set<String> roles);
    
    /**
     * Finds users who have had no activity after the specified cutoff date.
     * This includes checking both the joined_at and updated_at timestamps.
     *
     * @param cutoffDate The date before which users are considered for deletion
     * @param limit Maximum number of users to return
     * @return List of users with no recent activity since the cutoff date
     */
    @Query("SELECT u FROM User u WHERE u.joinedAt < :cutoffDate AND u.updatedAt < :cutoffDate " +
           "ORDER BY u.joinedAt ASC")
    List<User> findUsersWithNoRecentActivity(@Param("cutoffDate") Instant cutoffDate, @Param("limit") int limit);
}
