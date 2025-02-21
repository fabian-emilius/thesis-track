package de.tum.cit.aet.thesis.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for data retention functionality.
 * Maps to the 'data-retention' section in application.yml.
 */
@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "data-retention")
public class DataRetentionConfig {
    
    /**
     * Number of years after which inactive user data should be anonymized.
     * Must be a positive number.
     */
    @Min(value = 1, message = "Data retention period must be at least 1 year")
    private int userDataYears = 10;

    /**
     * Cron expression for scheduling the data retention job.
     * Default is 2 AM daily.
     */
    @NotBlank(message = "Schedule cron expression must not be blank")
    private String schedule = "0 0 2 * * *";

    /**
     * Number of users to process in each batch.
     * Must be a positive number to prevent memory issues with large datasets.
     */
    @Min(value = 1, message = "Batch size must be at least 1")
    private int batchSize = 100;
}
