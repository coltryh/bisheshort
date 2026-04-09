package com.ryh.shortlink.allinone.common.exception;

import com.ryh.shortlink.allinone.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        return Result.error(e.getMessage());
    }

    /**
     * 客户端异常处理
     */
    @ExceptionHandler(ClientException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleClientException(ClientException e, HttpServletRequest request) {
        log.warn("客户端异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        return Result.error(e.getMessage());
    }

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验异常: {}, URI: {}", errorMessage, request.getRequestURI());
        return Result.error("参数校验失败: " + errorMessage);
    }

    /**
     * 参数绑定异常处理
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e, HttpServletRequest request) {
        String errorMessage = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定异常: {}, URI: {}", errorMessage, request.getRequestURI());
        return Result.error("参数绑定失败: " + errorMessage);
    }

    /**
     * 参数类型不匹配异常处理
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String errorMessage = String.format("参数 '%s' 类型不匹配, 期望类型: %s",
                e.getName(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知");
        log.warn("参数类型不匹配: {}, URI: {}", errorMessage, request.getRequestURI());
        return Result.error(errorMessage);
    }

    /**
     * 缺少请求参数异常处理
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMissingParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        String errorMessage = String.format("缺少必需参数: %s", e.getParameterName());
        log.warn("缺少请求参数: {}, URI: {}", errorMessage, request.getRequestURI());
        return Result.error(errorMessage);
    }

    /**
     * 请求方法不支持异常处理
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<?> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String errorMessage = String.format("不支持的请求方法: %s", e.getMethod());
        log.warn("请求方法不支持: {}, URI: {}", errorMessage, request.getRequestURI());
        return Result.error(errorMessage);
    }

    /**
     * 404异常处理
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        String errorMessage = String.format("接口不存在: %s %s", e.getHttpMethod(), e.getRequestURL());
        log.warn("接口不存在: {}, URI: {}", errorMessage, request.getRequestURI());
        return Result.error(errorMessage);
    }

    /**
     * 空指针异常处理
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常: URI: {}", request.getRequestURI(), e);
        return Result.error("系统内部错误");
    }

    /**
     * 数组越界异常处理
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleIndexOutOfBoundsException(IndexOutOfBoundsException e, HttpServletRequest request) {
        log.error("数组越界异常: URI: {}", request.getRequestURI(), e);
        return Result.error("系统内部错误");
    }

    /**
     * 算术异常处理
     */
    @ExceptionHandler(ArithmeticException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleArithmeticException(ArithmeticException e, HttpServletRequest request) {
        log.error("算术异常: URI: {}", request.getRequestURI(), e);
        return Result.error("系统内部错误");
    }

    /**
     * 非法参数异常处理
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        return Result.error(e.getMessage());
    }

    /**
     * 非法状态异常处理
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {
        log.error("非法状态异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        return Result.error("系统内部错误");
    }

    /**
     * 数据库异常处理
     */
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleDataAccessException(org.springframework.dao.DataAccessException e, HttpServletRequest request) {
        log.error("数据库异常: URI: {}", request.getRequestURI(), e);
        return Result.error("数据库操作失败");
    }

    /**
     * 其他异常处理
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        log.error("未知异常: URI: {}", request.getRequestURI(), e);
        return Result.error("系统繁忙，请稍后重试");
    }
}
