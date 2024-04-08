package pers.zyx.shortlink.errorcode;

/**
 * 基础错误码
 */
public enum BaseErrorCode implements ErrorCode {

    CLIENT_ERROR("A000001", "用户端错误"),

    FLOW_LIMIT_ERROR("A000300", "当前系统繁忙，请稍后再试"),

    SERVICE_ERROR("B000001", "系统执行出错"),

    REMOTE_ERROR("C000001", "调用第三方服务出错");

    private final String code;

    private final String message;

    BaseErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}