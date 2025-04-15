package de.tum.cit.aet.thesis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import de.tum.cit.aet.thesis.entity.ThesisFile;
import de.tum.cit.aet.thesis.entity.User;

import java.util.List;
import java.util.UUID;


@Repository
public interface ThesisFileRepository extends JpaRepository<ThesisFile, UUID> {
    /**
     * Find files uploaded by a specific user
     * @param uploadedBy The uploader
     * @return List of files uploaded by the user
     */
    List<ThesisFile> findByUploadedBy(User uploadedBy);
}
