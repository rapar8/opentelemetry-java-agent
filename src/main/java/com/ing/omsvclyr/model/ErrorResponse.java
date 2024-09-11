package com.ing.omsvclyr.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String error;
    private String message;
    private HttpStatus httpStatus;
    private HttpStatus sourceHttpStatus;



    public ErrorResponse(String message, HttpStatus httpStatus, HttpStatus sourceHttpStatus, Exception ex) {

        this.httpStatus = httpStatus;
        this.sourceHttpStatus = sourceHttpStatus;
        this.message = message;
        this.error = ex.getMessage();
    }
}
