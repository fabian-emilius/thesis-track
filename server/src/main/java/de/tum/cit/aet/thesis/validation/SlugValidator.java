package de.tum.cit.aet.thesis.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class SlugValidator implements ConstraintValidator<ValidSlug, String> {
    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9-]+$");
    private static final int MAX_LENGTH = 255;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.length() <= MAX_LENGTH && SLUG_PATTERN.matcher(value).matches();
    }
}
