package com.tickets.ravetix.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Validator implementation for the {@link FutureAfterNow} annotation.
 * Ensures that the annotated date is in the future, with an optional minimum offset.
 */
public class FutureAfterNowValidator implements ConstraintValidator<FutureAfterNow, Object> {
    
    private int minutesInFuture;
    
    @Override
    public void initialize(FutureAfterNow constraintAnnotation) {
        this.minutesInFuture = constraintAnnotation.minutesInFuture();
    }
    
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null values
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (minutesInFuture > 0) {
            now = now.plusMinutes(minutesInFuture);
        }
        
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).isAfter(now);
        } else if (value instanceof java.util.Date) {
            return ((java.util.Date) value).toInstant()
                .isAfter(now.atZone(ZoneId.systemDefault()).toInstant());
        } else if (value instanceof java.time.LocalDate) {
            return ((java.time.LocalDate) value).atStartOfDay()
                .isAfter(now.toLocalDate().atStartOfDay());
        }
        
        return false;
    }
}
