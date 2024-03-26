package pers.zyx.shortlink.exception;

import pers.zyx.shortlink.errorcode.BaseErrorCode;
import pers.zyx.shortlink.errorcode.ErrorCode;

/**
 * 客户端异常
 */
public class ClientException extends AbstractException {

    public ClientException(ErrorCode errorCode) {
        this(null, null, errorCode);
    }

    public ClientException(String message) {
        this(message, null, BaseErrorCode.CLIENT_ERROR);
    }

    public ClientException(String message, ErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ClientException(String message, Throwable throwable, ErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "ClientException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}