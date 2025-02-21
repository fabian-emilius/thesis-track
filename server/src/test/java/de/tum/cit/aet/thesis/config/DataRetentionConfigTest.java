package de.tum.cit.aet.thesis.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataRetentionConfigTest {

    private Validator validator;
    private DataRetentionConfig config;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        config = new DataRetentionConfig();
    }

    @Test
    void whenAllValid_ShouldHaveNoViolations() {
        config.setUserDataYears(10);
        config.setBatchSize(100);
        config.setSchedule("0 0 2 * * *");

        var violations = validator.validate(config);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenUserDataYearsInvalid_ShouldHaveViolation() {
        config.setUserDataYears(0);
        config.setBatchSize(100);
        config.setSchedule("0 0 2 * * *");

        var violations = validator.validate(config);
        assertEquals(1, violations.size());
        assertEquals("Data retention period must be at least 1 year", 
            violations.iterator().next().getMessage());
    }

    @Test
    void whenBatchSizeInvalid_ShouldHaveViolation() {
        config.setUserDataYears(10);
        config.setBatchSize(0);
        config.setSchedule("0 0 2 * * *");

        var violations = validator.validate(config);
        assertEquals(1, violations.size());
        assertEquals("Batch size must be at least 1", 
            violations.iterator().next().getMessage());
    }

    @Test
    void whenScheduleBlank_ShouldHaveViolation() {
        config.setUserDataYears(10);
        config.setBatchSize(100);
        config.setSchedule("");

        var violations = validator.validate(config);
        assertEquals(1, violations.size());
        assertEquals("Schedule cron expression must not be blank", 
            violations.iterator().next().getMessage());
    }
}
