package roomescape.global.exception;

import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON_INVALID_INPUT", "요청 값을 확인해 주세요."),
    INVALID_JSON(HttpStatus.BAD_REQUEST, "COMMON_INVALID_JSON", "요청 본문 형식을 확인해 주세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    CommonErrorCode(
            HttpStatus status,
            String code,
            String message
    ) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus status() {
        return status;
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
