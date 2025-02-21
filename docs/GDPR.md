# GDPR Data Deletion Process

## Overview
The system implements automated GDPR-compliant data deletion for inactive user accounts. This document outlines the process and configuration options.

## Retention Period
- Default retention period: 10 years
- Configurable via `gdpr.retention.years` in application.yml

## Notification Period
- Users are notified 30 days before scheduled deletion
- Configurable via `gdpr.notification.days` in application.yml

## Process Flow
1. Daily check for inactive users (>10 years)
2. Schedule identified users for deletion
3. Send notification emails
4. Execute deletion after notification period

## Data Handling
### Anonymized Data
- First name -> [DELETED]
- Last name -> [DELETED]
- Email -> deleted-{uuid}@deleted.local
- Personal files removed

### Preserved Data
- Academic records
- Thesis relationships (anonymized)
- System metadata

## File Cleanup
User-related files are deleted from:
```
/uploads/
  ├── user_files/
  │   └── {user_id}/
  │       ├── cv/
  │       ├── degree/
  │       └── examination/
```

## Audit Logging
All deletions are logged in the `gdpr_deletion_log` table with:
- User ID
- Deletion timestamp
- Reason
- Additional metadata

## Configuration
In application.yml:
```yaml
gdpr:
  retention:
    years: 10  # Retention period in years
  notification:
    days: 30   # Notification period in days
```

## Monitoring
Monitor deletion activities through:
- Application logs
- gdpr_deletion_log table
- User activity timestamps

## Error Handling
- Failed deletions are logged
- Retry mechanism for file deletions
- Transaction management for data consistency

## Testing
Comprehensive test coverage includes:
- Unit tests for GDPRCleanupService
- Integration tests for complete workflow
- File system operation tests
