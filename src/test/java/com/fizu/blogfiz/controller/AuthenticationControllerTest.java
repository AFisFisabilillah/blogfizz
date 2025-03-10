package com.fizu.blogfiz.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fizu.blogfiz.dto.*;
import com.fizu.blogfiz.model.entity.ROLE;
import com.fizu.blogfiz.model.entity.User;
import com.fizu.blogfiz.model.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
    }

    @Test
    void registerTestSuccess()throws  Exception, IOException {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("afisfisabilillah21@gmail.com");;
        registerRequest.setName("Afis Fisabilllah");
        registerRequest.setPassword("211207bismillah");
        String request = objectMapper.writeValueAsString(registerRequest);
        mockMvc.perform(
                post("/auth/register")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk(),
                result -> {
                    String response = result.getResponse().getContentAsString();
                    WebResponse<String> responseRegister = objectMapper.readValue(response, new TypeReference<WebResponse<String>>() {
                    });
                    assertEquals(responseRegister.getMessage(), "Code Veirification sudah dikirim");
                    assertEquals(responseRegister.getStatus(), "succes");
                }
        );
    }

    @Test
    void registerTestBadReuest()throws IOException, Exception{
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("");;
        registerRequest.setName("");
        registerRequest.setPassword("211207bismillah");
        String request = objectMapper.writeValueAsString(registerRequest);
        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(request)
        ).andExpectAll(
                status().isBadRequest(),
                result -> {
                    ErrorResponse<Map<String, String>> errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ErrorResponse<Map<String, String>>>() {
                    });
                   assertEquals("field name tidak boleh kosong", errorResponse.getErrors().get("name"));
                   assertEquals("email tidak boleh kosong", errorResponse.getErrors().get("email"));
                }
        );
    }

    @Test
    void verifiyCodeSucces()throws IOException, Exception{
        User user = new User();
        user.setName("AfisFisabillah");
        user.setEmail("afis@gmial.com");
        user.setPassword("211207");
        user.setRole(ROLE.USER);
        user.setVerificationCode("211207");
        user.setVerivicationExpired(LocalDateTime.now().plusHours(1));
        user.setIsEnabled(false);
        userRepository.save(user);
        String request = objectMapper.writeValueAsString(VerifyRequest.builder().email(user.getEmail()).verificationCode(user.getVerificationCode()).build());
        mockMvc.perform(
                post("/auth/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(request)
        ).andExpectAll(

                status().isOk(),
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });
                    log.info("response : "+response);
                    assertEquals(response.getStatus(), "success");
                    assertEquals(response.getMessage(), "Akun Anda Sudah terverifikasi");
                    assertEquals(response.getData(),null);
                }
        );
    }

    @Test
    void verifiyCodeEmailNotFound()throws Exception{
        User user = new User();
        user.setName("AfisFisabillah");
        user.setEmail("afis@gmial.com");
        user.setPassword("211207");
        user.setRole(ROLE.USER);
        user.setVerificationCode("211207");
        user.setVerivicationExpired(LocalDateTime.now().plusHours(1));
        user.setIsEnabled(false);
        userRepository.save(user);
        String request = objectMapper.writeValueAsString(VerifyRequest.builder().email("afs@gmial.com").verificationCode("211207").build());
        mockMvc.perform(
                post("/auth/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(request)
        ).andExpectAll(
                status().isNotFound(),
                result -> {
                    ErrorResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ErrorResponse<String>>() {
                    });
                    assertEquals(response.getStatus(), "error");
                    assertEquals(response.getMessage(), "Maaf email tidak ketemu");
                }
        );
    }

    @Test
    void verifiyCodeWrong()throws Exception{
        User user = new User();
        user.setName("AfisFisabillah");
        user.setEmail("afis@gmial.com");
        user.setPassword("211207");
        user.setRole(ROLE.USER);
        user.setVerificationCode("211207");
        user.setVerivicationExpired(LocalDateTime.now().plusHours(1));
        user.setIsEnabled(false);
        userRepository.save(user);
        String request = objectMapper.writeValueAsString(VerifyRequest.builder().email("afis@gmial.com").verificationCode("2207").build());
        mockMvc.perform(
                post("/auth/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(request)
        ).andExpectAll(
                status().isBadRequest(),
                result -> {
                    ErrorResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ErrorResponse<String>>() {
                    });
                    assertEquals(response.getStatus(), "error");
                    assertEquals(response.getMessage(), "Maaf Kode salah");
                }
        );
    }

    @Test
    void testResendVeirciationCodeSucces()throws Exception{
        User user = new User();
        user.setName("AfisFisabillah");
        user.setEmail("joahn2112@gmail.com");
        user.setPassword("211207");
        user.setRole(ROLE.USER);
        user.setVerificationCode("211207");
        user.setVerivicationExpired(LocalDateTime.now().plusHours(1));
        user.setIsEnabled(false);
        userRepository.save(user);
        String request = objectMapper.writeValueAsString(new HashMap<String, String>(Map.of("email", user.getEmail())));

        mockMvc.perform(
                post("/auth/resend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(request)

        ).andExpectAll(
                status().isOk(),
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });
                    assertEquals("berhasil Mngirim Ulang Kode", response.getMessage());
                    assertEquals("success", response.getStatus());

                }
        );

    }

    @Test
    void testResendVeirciationCodeEmailNotFound()throws Exception{
        User user = new User();
        user.setName("AfisFisabillah");
        user.setEmail("joahn2112@gmail.com");
        user.setPassword("211207");
        user.setRole(ROLE.USER);
        user.setVerificationCode("211207");
        user.setVerivicationExpired(LocalDateTime.now().plusHours(1));
        user.setIsEnabled(false);
        userRepository.save(user);
        String request = objectMapper.writeValueAsString(new HashMap<String, String>(Map.of("email", "asad@gmial.com")));

        mockMvc.perform(
                post("/auth/resend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(request)

        ).andExpectAll(
                status().isNotFound(),
                result -> {
                    ErrorResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ErrorResponse<String>>() {
                    });
                    assertEquals("Maaf email tidak ketemu", response.getMessage());
                    assertEquals("error", response.getStatus());

                }
        );

    }

    @Test
    void testLoginSucces()throws Exception{
        User user = new User();
        String password = "211207";
        user.setName("AfisFisabillah");
        user.setEmail("joahn2112@gmail.com");
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setRole(ROLE.USER);
        user.setVerificationCode("211207");
        user.setVerivicationExpired(LocalDateTime.now().plusHours(1));
        user.setIsEnabled(true);
        userRepository.save(user);
        LoginRequest request = LoginRequest.builder().email(user.getEmail()).password(password).build();
        String request2 = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(request2)

        ).andExpectAll(
                status().isOk(),
                result -> {
                    WebResponse<Map<String,String>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<Map<String,String>>>() {
                    });
                    assertEquals("berhasil Login", response.getMessage());
                    assertEquals("succes", response.getStatus());
                    assertEquals(user.getEmail(), response.getData().get("email"));
                    log.info("token : "+response.getData().get("token"));
                }
        );

    }

}