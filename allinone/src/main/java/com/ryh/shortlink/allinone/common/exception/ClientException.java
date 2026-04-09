package com.ryh.shortlink.allinone.common.exception;

/**
 * 客户端异常（参数校验等）
 */
public class ClientException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private String code;

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
