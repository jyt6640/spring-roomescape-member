package roomescape.global.exception;

public class ReservationTimeException extends RuntimeException{

    private final ErrorCode errorCode;

    public ReservationTimeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
