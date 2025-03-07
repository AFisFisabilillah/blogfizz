package com.fizu.blogfiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorResponse<T>  {
    private String status;
    private String message;
    private T errors;
}
