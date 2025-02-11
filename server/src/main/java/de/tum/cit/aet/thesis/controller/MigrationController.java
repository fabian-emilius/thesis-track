package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.service.DataMigrationService;
import de.tum.cit.aet.thesis.service.DataMigrationService.MigrationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v2/admin/migration")
@RequiredArgsConstructor
public class MigrationController {
    private final DataMigrationService migrationService;

    @PostMapping("/groups")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> migrateToGroups() {
        migrationService.migrateToGroups();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/groups/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MigrationStatus> getMigrationStatus() {
        return ResponseEntity.ok(migrationService.validateMigration());
    }
}