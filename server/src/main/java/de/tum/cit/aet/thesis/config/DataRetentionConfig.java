package de.tum.cit.aet.thesis.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "thesis-management.data-retention")
public class DataRetentionConfig {
    /**
     * Number of years to retain user data (default: 10 years per GDPR)
     */
    private int userDataYears = 10;
    
    /**
     * Cron expression for scheduling the data retention job
     * Default is 2 AM daily
     */
    private String schedule = "0 0 2 * * *";
    
    /**
     * Number of users to process in each batch to avoid memory issues
     */
    private int batchSize = 100;
}