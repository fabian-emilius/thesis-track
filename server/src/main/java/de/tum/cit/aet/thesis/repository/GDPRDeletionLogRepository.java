package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.GDPRDeletionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GDPRDeletionLogRepository extends JpaRepository<GDPRDeletionLog, UUID> {
}
