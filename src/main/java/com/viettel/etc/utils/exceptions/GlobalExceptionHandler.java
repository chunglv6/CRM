package com.viettel.etc.utils.exceptions;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.viettel.etc.dto.boo.ResExceptionBooDTO;
import com.viettel.etc.services.JedisCacheService;
import com.viettel.etc.utils.FnCommon;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    JedisCacheService jedisCacheService;

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleValidateException(ConstraintViolationException e) {
        LOG.error("Has ERROR", e);
        String mess = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage).findFirst().orElse("");
        return new ResponseEntity<>(FnCommon.responseToClient(HttpStatus.BAD_REQUEST.value(), mess), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOG.error("Has ERROR", ex);
        String mess = "";
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            mess = error.getField() + ": " + error.getDefaultMessage();
            break;
        }
        return new ResponseEntity<>(FnCommon.responseToClient(HttpStatus.BAD_REQUEST.value(), mess), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOG.error("Has ERROR", ex);
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException cause = (InvalidFormatException) ex.getCause();
            for (JsonMappingException.Reference path : cause.getPath()) {
                String mess = path.getFieldName() + ": Invalid format";
                return new ResponseEntity<>(FnCommon.responseToClient(HttpStatus.BAD_REQUEST.value(), mess), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(FnCommon.responseToClient(jedisCacheService.getCodeErrorByKey("crm.internal.server"), jedisCacheService.getMessageErrorByKey("crm.internal.server")), HttpStatus.OK);
    }

    @ExceptionHandler(EtcException.class)
    public ResponseEntity<Object> handleETCException(EtcException ex) {
        LOG.error("Has ERROR", ex);
        if (Objects.isNull(ex.getErrorApp())) {
            if (Objects.nonNull(ex.getCodeError())) {
                return new ResponseEntity<>(FnCommon.responseToClient(jedisCacheService.getCodeErrorByKey(ex.getMessage()), jedisCacheService.getMessageErrorByKey(ex.getMessage())), HttpStatus.OK);
            }

            if (Objects.nonNull(ex.getErrorMapKey())) {
                return new ResponseEntity<>(FnCommon.responseToClient(jedisCacheService.getCodeErrorByKey(ex.getMessage()), jedisCacheService.replaceMessWithValues(ex.getMessage(), ex.getErrorMapKey())), HttpStatus.OK);
            }
            return new ResponseEntity<>(FnCommon.responseToClient(HttpStatus.BAD_REQUEST.value(), jedisCacheService.getMessageErrorByKey(ex.getMessage())), HttpStatus.OK);
        }
        return new ResponseEntity<>(FnCommon.responseToClient(ex), HttpStatus.OK);
    }

    @ExceptionHandler(BooException.class)
    public ResponseEntity<Object> handleBooException(BooException ex) {
        LOG.error("Has BOO exception", ex);
        int status = ex.getHttpCode() == null ? HttpStatus.BAD_REQUEST.value() : ex.getHttpCode();
        if (!FnCommon.isNullOrEmpty(ex.getMessage())) {
            return new ResponseEntity<>(new ResExceptionBooDTO(ex.getTimestamp(), String.valueOf(jedisCacheService.getCodeErrorByKey(ex.getMessage())), ex.getError(), jedisCacheService.getMessageErrorByKey(ex.getMessage())), HttpStatus.valueOf(status));
        }
        return new ResponseEntity<>(FnCommon.responseToClient(ex), HttpStatus.valueOf(status));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        LOG.error("Has ERROR", ex);
        return new ResponseEntity<>(FnCommon.responseToClient(jedisCacheService.getCodeErrorByKey("crm.internal.server"), jedisCacheService.getMessageErrorByKey("crm.internal.server")), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Object> handleDataNotFoundException(DataNotFoundException ex) {
        LOG.error("Has ERROR", ex);
        return new ResponseEntity<>(FnCommon.responseToClient(ex.getCode(), jedisCacheService.getMessageErrorByKey(ex.getMessage())), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        System.out.println("Missing parameter");
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOG.error("No handler found exception", ex);
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOG.error("Handle type mismatch", ex);
        return handleExceptionInternal(ex, null, headers, status, request);
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)   //(1)
    public Object exceptionHandler(IOException e, HttpServletRequest request) {
        if (StringUtils.containsIgnoreCase(ExceptionUtils.getRootCauseMessage(e), "Broken pipe")) {   //(2)
            return null;        //(2)	socket is closed, cannot return any response
        } else {
            return new HttpEntity<>(e.getMessage());  //(3)
        }
    }

    @Override
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ResExceptionBooDTO(System.currentTimeMillis(), jedisCacheService.getMessageErrorByKey("crm.boo.method.not_allowed")));
    }
}
