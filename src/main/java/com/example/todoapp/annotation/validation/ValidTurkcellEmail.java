package com.example.todoapp.annotation.validation; // Yeni paket

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TurkcellEmailValidator.class)
public @interface ValidTurkcellEmail {

    String message() default "Geçerli bir Turkcell e-posta adresi değildir (örn: @turkcell.com.tr)";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}