# Data Retention Policy

## Overview
The system implements GDPR-compliant data retention policies for user data. Personal data is automatically anonymized after 10 years of inactivity.

## Configuration
Data retention settings can be configured in `application.yml`:

```yaml
data-retention:
  user-data-years: 10 # Number of years before data anonymization
  schedule: "0 0 2 * * *" # Cron schedule (default: 2 AM daily)
  batch-size: 100 # Number of users to process in each batch
```

## Data Handling

### Personal Data (Deleted)
- Email address
- First name
- Last name
- Gender
- Nationality
- Matriculation number
- CV file
- Degree certificates
- Examination documents
- Avatar
- Custom data

### Academic Data (Anonymized)
- Thesis records
- Applications
- Comments
- Presentations

## Process
1. The system identifies users with no activity for 10+ years
2. Personal data is anonymized
3. Associated files are physically deleted
4. Academic records are maintained but anonymized
5. All operations are logged for audit purposes

## Logging
All data retention operations are logged with the following information:
- User ID
- Operation timestamp
- Operation type
- Operation status
- Error details (if any)

## Compliance
This implementation complies with GDPR Article 17 (Right to be forgotten) by:
- Implementing secure data deletion
- Maintaining audit trails
- Ensuring data minimization
- Providing configurable retention periods
