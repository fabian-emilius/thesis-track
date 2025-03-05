# User Data Retention Service

## Overview
The `UserDataRetentionService` implements GDPR-compliant data retention policies for the Thesis Management System. It automatically identifies and anonymizes user data that exceeds the defined retention period (default: 10 years).

## Features

### Automated Data Cleanup
- **Scheduled Execution**: Runs daily at 2 AM by default (configurable)
- **Batch Processing**: Processes users in configurable batch sizes to minimize system impact
- **Error Handling**: Robust error handling with detailed logging

### Data Handling

1. **Personal Data (Completely Anonymized)**:
   - Email (replaced with anonymized version)
   - Names (replaced with "Anonymized User")
   - Gender, nationality, matriculation number (removed)
   - User files (CV, degree, examination reports physically deleted)

2. **Academic Data (Preserved)**:
   - Thesis records
   - Applications 
   - Comments
   - Presentations

## Configuration

The service is configurable through `application.yml` or environment variables:

```yaml
data-retention:
  user-data-years: ${DATA_RETENTION_YEARS:10}  # Retention period in years
  schedule: ${DATA_RETENTION_SCHEDULE:0 0 2 * * *}  # Cron expression for scheduling
  batch-size: ${DATA_RETENTION_BATCH_SIZE:100}  # Number of users to process in each batch
```

### Environment Variables

- `DATA_RETENTION_YEARS`: Number of years to retain user data (default: 10)
- `DATA_RETENTION_SCHEDULE`: Cron expression for the cleanup schedule (default: daily at 2 AM)
- `DATA_RETENTION_BATCH_SIZE`: Number of users to process in each batch (default: 100)

## Implementation Details

### Identifying Users for Deletion
Users are identified for anonymization based on two criteria:
1. Their `joinedAt` date is older than the configured retention period
2. Their `updatedAt` date is also older than the retention period (ensuring no recent activity)

### Anonymization Process
1. Delete user files from the filesystem
2. Anonymize personal information in the user record
3. Mark the record as anonymized with metadata (timestamp)
4. Preserve academic records for historical and statistical purposes

### Logging
Detailed logging is implemented to create an audit trail of all data anonymization activities.

## Compliance

This implementation helps meet GDPR Article 17 (Right to erasure) requirements by:
- Identifying data that exceeds retention periods
- Removing personal identifiers while preserving necessary academic records
- Creating logs for compliance verification
