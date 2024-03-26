package pers.zyx.shortlink.exception;

import lombok.Getter;
import org.springframework.util.StringUtils;
import pers.zyx.shortlink.errorcode.ErrorCode;

import java.util.Optional;

/**
 * 异常规约
 *
 * @see ClientException
 * @see ServiceException
 * @see RemoteException
 */
@Getter
public abstract class AbstractException extends RuntimeException {

    public final String errorCode;

    public final String errorMessage;

    public AbstractException(String message, Throwable throwable, ErrorCode errorCode) {
        super(message, throwable);
        this.errorCode = errorCode.code();
        this.errorMessage = Optional.ofNullable(StringUtils.hasLength(message) ? message : null).orElse(errorCode.message());
    }
}