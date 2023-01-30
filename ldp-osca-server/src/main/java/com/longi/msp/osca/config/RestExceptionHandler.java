package com.longi.msp.osca.config;

import com.meicloud.paas.common.ReturnT;
import com.meicloud.paas.common.error.ErrorCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author liangkd
 */
@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ReturnT<String> exception(Exception ex) {
        return ReturnT.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }

    @ExceptionHandler(ErrorCodeException.class)
    @ResponseStatus(HttpStatus.OK)
    public ReturnT<String> exception(ErrorCodeException ex) {
        return ReturnT.failed(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

}
