package com.safronov.spring.mvc.untitled.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckEmailValidator.class)
public @interface CheckEmail {

    public String value() default "qa.automation.ru";
    public String message() default "Email должен заканчиваться на qa.automation.ru";

    public Class<?>[] groups() default {};
    public Class<? extends Payload>[] payload() default {};

}
