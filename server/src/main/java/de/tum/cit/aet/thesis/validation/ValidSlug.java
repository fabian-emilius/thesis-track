package de.tum.cit.aet.thesis.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SlugValidator.class)
@Documented
public @interface ValidSlug {
    String message() default "Invalid slug format. Use only lowercase letters, numbers, and hyphens";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
