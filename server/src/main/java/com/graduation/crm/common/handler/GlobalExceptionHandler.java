package com.graduation.crm.common.handler;

import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
     * PathVariable/RequestParam 类型转换失败时返回明确的参数错误，避免无效 id 触发系统异常日志。
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("Request parameter type mismatch, name={}, value={}, requiredType={}",
                e.getName(), e.getValue(), e.getRequiredType() == null ? null : e.getRequiredType().getSimpleName());
        return Result.fail(400, "请求参数格式错误");
    }

    /**
     * 未预期异常不直接暴露堆栈信息，但必须写入日志，方便定位线上问题。
     */
    /**
     * 上传内容超过 multipart 限制时返回明确提示，避免进入系统异常分支。
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("Upload request exceeded max size: {}", e.getMessage());
        return Result.fail(400, "上传文件过大，请压缩后重试");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("Unexpected system exception", e);
        return Result.fail(500, "系统异常");
    }
}
