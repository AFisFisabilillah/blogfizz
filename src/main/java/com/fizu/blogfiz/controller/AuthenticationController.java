package com.fizu.blogfiz.controller;

import com.fizu.blogfiz.dto.LoginRequest;
import com.fizu.blogfiz.dto.RegisterRequest;
import com.fizu.blogfiz.dto.VerifyRequest;
import com.fizu.blogfiz.dto.WebResponse;
import com.fizu.blogfiz.model.entity.User;
import com.fizu.blogfiz.service.AuthenticateService;
import com.fizu.blogfiz.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import javax.print.attribute.standard.Media;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthenticationController {
    @Autowired
    private AuthenticateService authenticateService;
    @Autowired
    private JwtService jwtService;

    @PostMapping(
            path = "/auth/register",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody RegisterRequest request){
        authenticateService.register(request);
        WebResponse<String> response = WebResponse
                .<String>builder()
                .status("succes")
                .message("Code Veirification sudah dikirim")
                .build();
        return response;
    }

    @PostMapping(
            path = "/auth/resend",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> resend(@RequestBody String email){
        authenticateService.resendVerificationCode(email);
        return WebResponse.<String>builder().message("berhasil Mngirim Ulang Kode").status("success").build();
    }

    @PostMapping(
            path = "/auth/verify",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> resend(@RequestBody VerifyRequest request){
        authenticateService.verifiyUser(request);
        return WebResponse.<String>builder().message("berhasil Mngirim Ulang Kode").status("success").build();
    }

    @PostMapping(
            path = "/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<Map<String, String>> login(@RequestBody(required = true)LoginRequest request){
        User login = authenticateService.login(request);
        String token = jwtService.generateToken(login);
        Map<String, String> response = new HashMap<>();
        response.put("email", login.getEmail());
        response.put("token" , token);

        return WebResponse.<Map<String, String>>builder().status("succes").message("berhasi; Login").data(response).build();
    }

    @GetMapping(
            path = "/auth/user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserDetails user(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails;    }
}
