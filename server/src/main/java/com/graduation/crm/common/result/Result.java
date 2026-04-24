package com.graduation.crm.common.result;

import lombok.Data;

@Data
public class Result<T> {

    /**
     * 前后端统一响应格式，前端只需要判断 code 是否为 200。
     */
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static Result<Void> success() {
        Result<Void> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        return result;
    }

    public static Result<Void> fail(Integer code, String message) {
        Result<Void> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
