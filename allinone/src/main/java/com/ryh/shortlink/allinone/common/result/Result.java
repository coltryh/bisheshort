package com.ryh.shortlink.allinone.common.result;

import lombok.Data;

@Data
public class Result<T> {

    private String code;
    private String message;
    private T data;

    public static final String SUCCESS_CODE = "0";
    public static final String ERROR_CODE = "1";

    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS_CODE);
        result.setMessage("操作成功");
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS_CODE);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(ERROR_CODE);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(String code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
