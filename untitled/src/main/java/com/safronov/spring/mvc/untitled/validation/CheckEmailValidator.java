package com.safronov.spring.mvc.untitled.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckEmailValidator implements ConstraintValidator<CheckEmail, String> {

    private String endOfEmail;


    @Override
    public void initialize(CheckEmail checkEmail) {
        endOfEmail = checkEmail.value();
    }

    @Override
    public boolean isValid(String enteredEmail,  ConstraintValidatorContext constraintValidatorContext) {

        return enteredEmail.endsWith(endOfEmail);
    }

}
