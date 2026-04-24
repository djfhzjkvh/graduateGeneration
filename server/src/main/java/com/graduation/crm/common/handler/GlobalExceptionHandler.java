package com.graduation.crm.common.handler;

import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常统一返回给前端，避免 Controller 中重复包装错误响应。
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("Business exception: {}", e.getMessage());
        return Result.fail(500, e.getMessage());
    }

    /**
     * 参数校验异常优先返回具体字段提示，方便前端直接展示给用户。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = "参数校验失败";
        if (e.getBindingResult().getFieldError() != null) {
            message = e.getBindingResult().getFieldError().getDefaultMessage();
        }
        log.warn("Validation exception: {}", message);
        return Result.fail(400, message);
    }

    /**
     * 未预期异常不直接暴露堆栈信息，但必须写入日志，方便定位线上问题。
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("Unexpected system exception", e);
        return Result.fail(500, "系统异常");
    }
}
