package com.fizu.blogfiz.validation.constraint;
import com.fizu.blogfiz.validation.annotation.Unique;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueConstraint implements ConstraintValidator<Unique, String> {

    @Autowired
    private EntityManager entityManager;

    private Class table;
    private String field;
    private String message;

    @Override
    public void initialize(Unique constraintAnnotation) {
        this.table = constraintAnnotation.entity();
        this.field = constraintAnnotation.field();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        entityManager.createNamedQuery("SELECT COUNT("+field+") FROM "+table.getSimpleName());
        return false;
    }
}
