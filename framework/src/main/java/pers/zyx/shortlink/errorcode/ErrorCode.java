package pers.zyx.shortlink.errorcode;

/**
 * 错误码规约
 */
public interface ErrorCode {

    /**
     * 错误码
     */
    String code();

    /**
     * 错误信息
     */
    String message();
}