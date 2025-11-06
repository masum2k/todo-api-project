package com.example.todoapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TurkcellEmailValidator implements ConstraintValidator<ValidTurkcellEmail, String> {

    private static final String ALLOWED_DOMAIN = "turkcell.com.tr";

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {

        if (email == null || email.isBlank()) {
            return true;
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return false;
        }

        String domain = email.substring(atIndex + 1);

        return ALLOWED_DOMAIN.equalsIgnoreCase(domain);
    }
}