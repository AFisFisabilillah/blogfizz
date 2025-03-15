package com.fizu.blogfiz.validation.constraint;
import com.fizu.blogfiz.validation.annotation.Unique;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
        String schema = table.getSimpleName();
        String colom = field.toLowerCase();
        TypedQuery<Long> queryUnique = entityManager.createQuery("SELECT COUNT(s) FROM " + schema + " s WHERE s." + colom + " = :value", Long.class);
        queryUnique.setParameter("value", value);
        Long unique = queryUnique.getSingleResult();

        return unique == 0;

    }
}
