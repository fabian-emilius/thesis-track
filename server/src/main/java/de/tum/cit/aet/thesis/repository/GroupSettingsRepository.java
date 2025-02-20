package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.GroupSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupSettingsRepository extends JpaRepository<GroupSettings, UUID> {
}