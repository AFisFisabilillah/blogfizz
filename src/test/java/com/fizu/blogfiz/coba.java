package com.fizu.blogfiz;

import com.fizu.blogfiz.dto.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest
public class coba {
    @Autowired
    private Validator validator;

    @Test
    void test(){
        RegisterRequest registerRequest = new RegisterRequest();
        Set<ConstraintViolation<RegisterRequest>> validate = validator.validate(registerRequest);
        validate.forEach(violation ->{
            System.out.println(violation.getPropertyPath().toString() + " : "+violation.getMessage());
        });
    }
}
