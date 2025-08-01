package com.safronov.spring.mvc.untitled.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Валидатор для проверки email адреса
 * Проверяет, что email заканчивается на указанный домен
 */
public class CheckEmailValidator implements ConstraintValidator<CheckEmail, String> {

    private String endOfEmail;

    @Override
    public void initialize(CheckEmail checkEmail) {
        endOfEmail = checkEmail.value();
    }

    @Override
    public boolean isValid(String enteredEmail, ConstraintValidatorContext constraintValidatorContext) {
        if (enteredEmail == null || enteredEmail.trim().isEmpty()) {
            return false;
        }
        return enteredEmail.toLowerCase().endsWith(endOfEmail.toLowerCase());
    }
}
