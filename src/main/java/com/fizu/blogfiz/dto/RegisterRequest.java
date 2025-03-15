package com.fizu.blogfiz.dto;

import com.fizu.blogfiz.model.entity.User;
import com.fizu.blogfiz.validation.annotation.Unique;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank(message = "email tidak boleh kosong")
    @Email
    @Unique(field = "email", entity = User.class)
    private String email;
    @NotBlank(message = "password tidak boleh kosong")
    @Size(min = 8, message = "password harus lebih dari 8 karakter")
    private String password;
    @NotBlank(message = "field name tidak boleh kosong")
    private String name;
}
