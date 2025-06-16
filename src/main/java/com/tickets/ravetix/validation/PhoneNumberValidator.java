package com.tickets.ravetix.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Validates phone numbers according to international standards.
 * Supports formats like:
 * - +51 987 654 321
 * +51 987654321
 * 987654321
 * 987 654 321
 */
public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    
    // Pattern explanation:
    // ^\+? - Optional + at the start
    // (\d{1,3})? - Optional country code (1-3 digits)
    // [\s-]? - Optional separator (space or dash)
    // (\(\d{1,4}\)[\s-]?)? - Optional area code in parentheses
    // [\d\s-]{6,} - 6 or more digits, spaces or dashes
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\+?(\\d{1,3})?[\\s-]?(\\(\\d{1,4}\\)[\\s-]?)?[\\d\\s-]{6,}$");
    
    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // Let @NotBlank handle empty values
        }
        
        // Remove all whitespace and special characters except + and digits
        String normalized = value.replaceAll("[^\\d+]", "");
        
        // Check if it's a valid phone number
        return PHONE_PATTERN.matcher(value).matches() && 
               normalized.length() >= 9 && 
               normalized.length() <= 15;
    }
}
