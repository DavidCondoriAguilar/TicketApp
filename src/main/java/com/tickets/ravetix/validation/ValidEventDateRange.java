package com.tickets.ravetix.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validates that the end date is after the start date for an event.
 * Should be applied at the class level of an event DTO.
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventDateRangeValidator.class)
@Documented
public @interface ValidEventDateRange {
    String message() default "La fecha de fin debe ser posterior a la fecha de inicio";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * @return The start date field name
     */
    String startDate() default "fechaHoraInicio";
    
    /**
     * @return The end date field name
     */
    String endDate() default "fechaHoraFin";
    
    /**
     * @return Whether to allow start and end dates to be equal
     */
    boolean allowEqual() default false;
}
