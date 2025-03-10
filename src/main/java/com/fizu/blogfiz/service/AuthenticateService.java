package com.fizu.blogfiz.service;

import com.fizu.blogfiz.dto.LoginRequest;
import com.fizu.blogfiz.dto.VerifyRequest;
import com.fizu.blogfiz.exception.ResponseException;
import com.fizu.blogfiz.model.entity.ROLE;
import com.fizu.blogfiz.model.entity.User;
import com.fizu.blogfiz.dto.RegisterRequest;
import com.fizu.blogfiz.model.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;

@Slf4j
@Service
public class AuthenticateService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private Validator validator;
    @Autowired
    private AuthenticationManager authenticationManager;

    public User register(RegisterRequest request){
        Set<ConstraintViolation<RegisterRequest>> validate = validator.validate(request);
        if(validate.size() > 0) throw new ConstraintViolationException(validate);
        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResponseException("Email SUdah digunakan", HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setRole(ROLE.USER);
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setVerivicationExpired(LocalDateTime.now().plusHours(1));
        user.setVerificationCode(generateVerificationCode());
        user.setIsEnabled(false);

        userRepository.save(user);
        sendVerificationEmail(user);
        return new User();
    }

    @Transactional
    public void verifiyUser(VerifyRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResponseException("Maaf email tidak ketemu", HttpStatus.NOT_FOUND));

        if(user.getVerivicationExpired().isBefore(LocalDateTime.now())){
            throw new ResponseException("Maaf token sudah expired ", HttpStatus.BAD_REQUEST);
        }

        if(user.getVerificationCode().equals(request.getVerificationCode())){
            user.setIsEnabled(true);
            userRepository.save(user);
        }else {
            throw new ResponseException("Maaf Kode salah", HttpStatus.BAD_REQUEST);
        }

    }

    @Transactional
    public void resendVerificationCode(String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseException("Maaf email tidak ketemu", HttpStatus.NOT_FOUND));
        user.setVerificationCode(generateVerificationCode());
        user.setVerivicationExpired(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        sendVerificationEmail(user);
    }


    public void sendVerificationEmail(User user){
        String verificationCode = user.getVerificationCode();
        String to = user.getEmail();
        String subject = user.getName();
        String text ="<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try{
            emailService.sendEmailVerificationTo(to,subject, text);
        } catch (MessagingException e) {
            throw new ResponseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public User login(LoginRequest request){
        Set<ConstraintViolation<LoginRequest>> validate = validator.validate(request);
        if(validate.size()>0){
            throw new ConstraintViolationException(validate);
        }
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResponseException("Maaf email atau Paasword Salah", HttpStatus.UNAUTHORIZED));

//        Mengecek apakh Akun Sudah Terverifikasi?
        if(!user.isEnabled()){
            throw new ResponseException("Maaf email elum Terverifikasi", HttpStatus.UNAUTHORIZED);
        }


//        mengecek Login
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),
                request.getPassword()));

        log.info("berhasil login");

        return user;
    }

    public String generateVerificationCode(){
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }


}
