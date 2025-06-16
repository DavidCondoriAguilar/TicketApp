package com.tickets.ravetix.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalDateTime;

/**
 * Validator for {@link ValidEventDateRange} annotation.
 * Ensures that the end date is after the start date.
 */
public class EventDateRangeValidator implements ConstraintValidator<ValidEventDateRange, Object> {

    private String startDateField;
    private String endDateField;
    private boolean allowEqual;

    @Override
    public void initialize(ValidEventDateRange constraint) {
        this.startDateField = constraint.startDate();
        this.endDateField = constraint.endDate();
        this.allowEqual = constraint.allowEqual();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            BeanWrapper beanWrapper = new BeanWrapperImpl(value);
            
            Object startDateObj = beanWrapper.getPropertyValue(startDateField);
            Object endDateObj = beanWrapper.getPropertyValue(endDateField);

            // If either date is null, defer to @NotNull validation
            if (startDateObj == null || endDateObj == null) {
                return true;
            }

            if (!(startDateObj instanceof LocalDateTime) || !(endDateObj instanceof LocalDateTime)) {
                throw new IllegalArgumentException("The annotated fields must be of type LocalDateTime");
            }

            LocalDateTime startDate = (LocalDateTime) startDateObj;
            LocalDateTime endDate = (LocalDateTime) endDateObj;

            if (allowEqual) {
                return !endDate.isBefore(startDate);
            } else {
                return endDate.isAfter(startDate);
            }
            
        } catch (Exception e) {
            // Log the error for debugging purposes
            System.err.println("Error validating date range: " + e.getMessage());
            return false;
        }
    }
}
