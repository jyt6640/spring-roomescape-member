package roomescape.global.exception;

public class ThemeException extends RuntimeException {

    private final ErrorCode errorCode;

    public ThemeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
