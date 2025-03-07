package com.fizu.blogfiz.validation.annotation;

import com.fizu.blogfiz.validation.constraint.UniqueConstraint;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = UniqueConstraint.class)
public @interface Unique {
    String message() default "Data Ini sudah ada dalam tabel";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class entity() ;

    String field() default  "";

}
