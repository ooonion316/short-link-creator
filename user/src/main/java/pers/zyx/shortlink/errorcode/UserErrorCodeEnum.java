package pers.zyx.shortlink.errorcode;

/**
 * 用户错误码枚举
 */
public enum UserErrorCodeEnum implements ErrorCode {
    USER_NULL("B000200", "用户不存在"),

    USER_EXIST("B000201", "用户已存在"),

    USER_SAVE_ERROR("B000202", "用户记录保存失败");

    private final String code;

    private final String message;

    UserErrorCodeEnum(String code, String message) {
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