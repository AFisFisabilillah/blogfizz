package com.fizu.blogfiz.validation.constraint;

import com.fizu.blogfiz.dto.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class UniqueConstraintTest {

    @Autowired
    private Validator validator;

    @Test
    void testUniqueValidation(){
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPassword("ewqewdsadsadsaq");
        registerRequest.setEmail("afis.fisabillah6@smk.belajar.id");
        registerRequest.setName("afis fisabillah");
        Set<ConstraintViolation<RegisterRequest>> validate = validator.validate(registerRequest);
        assertTrue(validate.size() > 0);
        for (ConstraintViolation violation : validate){
            log.info("message : "+violation.getMessage());
            log.info("Path : "+violation.getPropertyPath());

        }
    }

}