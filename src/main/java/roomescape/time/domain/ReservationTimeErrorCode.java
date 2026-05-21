package roomescape.time.domain;

import org.springframework.http.HttpStatus;
import roomescape.global.exception.ErrorCode;

public enum ReservationTimeErrorCode implements ErrorCode {

    INVALID_START_AT(HttpStatus.BAD_REQUEST, "TIME_INVALID_START_AT", "예약 시간 형식은 HH:mm이어야 합니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "TIME_NOT_FOUND", "예약 시간을 찾을 수 없습니다."),
    RESERVED_TIME(HttpStatus.BAD_REQUEST, "TIME_RESERVED", "예약이 존재하는 시간은 삭제할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ReservationTimeErrorCode(
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
