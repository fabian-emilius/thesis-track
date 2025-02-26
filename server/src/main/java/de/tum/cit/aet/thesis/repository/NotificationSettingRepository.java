package de.tum.cit.aet.thesis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import de.tum.cit.aet.thesis.entity.NotificationSetting;
import de.tum.cit.aet.thesis.entity.key.NotificationSettingId;

import java.util.UUID;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, NotificationSettingId> {
    /**
     * Deletes all notification settings for a user.
     * Used for GDPR compliance when anonymizing user data.
     *
     * @param userId The user ID
     */
    @Modifying
    @Query("DELETE FROM NotificationSetting ns WHERE ns.id.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
