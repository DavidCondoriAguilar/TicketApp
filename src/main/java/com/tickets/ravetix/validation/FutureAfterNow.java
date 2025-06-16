package com.tickets.ravetix.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validates that the annotated date is in the future relative to the current moment.
 * Can be applied to LocalDateTime, LocalDate, and other date/time types.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FutureAfterNowValidator.class)
@Documented
public @interface FutureAfterNow {
    String message() default "La fecha debe ser futura";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    /**
     * @return number of minutes to add to the current time for the minimum allowed date
     */
    int minutesInFuture() default 0;
}
