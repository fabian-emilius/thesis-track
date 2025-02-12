package de.tum.cit.aet.thesis.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import de.tum.cit.aet.thesis.entity.Topic;

import java.util.UUID;

@Repository
public interface TopicRepository  extends JpaRepository<Topic, UUID>  {
    @Query(value =
            "SELECT DISTINCT t.* FROM topics t " +
            "LEFT JOIN group_members gm ON t.group_id = gm.group_id " +
            "WHERE (:groupId IS NULL OR t.group_id = :groupId OR gm.member_id = :groupId) AND " +
            "(:searchQuery IS NULL OR t.title ILIKE CONCAT('%', :searchQuery, '%')) AND " +
            "(t.thesis_types IS NULL OR CAST(:types AS TEXT[]) IS NULL OR t.thesis_types && CAST(:types AS TEXT[])) AND " +
            "(:includeClosed = TRUE OR t.closed_at IS NULL)",
            nativeQuery = true
    )
    Page<Topic> searchTopics(
            @Param("groupId") UUID groupId,
            @Param("types") String[] types,
            @Param("includeClosed") boolean includeClosed,
            @Param("searchQuery") String searchQuery,
            Pageable page
    );

    @Query("SELECT COUNT(DISTINCT t) FROM Topic t LEFT JOIN GroupMember gm ON t.group = gm.group WHERE t.closedAt IS NULL AND (:groupId IS NULL OR t.group.id = :groupId OR gm.member.id = :groupId)")
    long countOpenTopics(@Param("groupId") UUID groupId);

    @Query("SELECT DISTINCT t FROM Topic t LEFT JOIN GroupMember gm ON t.group = gm.group WHERE t.group.id = :groupId OR gm.member.id = :groupId")
    Page<Topic> findByGroupId(@Param("groupId") UUID groupId, Pageable pageable);

    @Query("SELECT DISTINCT t FROM Topic t LEFT JOIN GroupMember gm ON t.group = gm.group WHERE (t.group.id = :groupId OR gm.member.id = :groupId) AND t.closedAt IS NULL")
    Page<Topic> findOpenTopicsByGroupId(@Param("groupId") UUID groupId, Pageable pageable);
}
