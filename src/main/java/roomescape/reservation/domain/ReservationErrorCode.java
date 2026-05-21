package roomescape.reservation.domain;

import org.springframework.http.HttpStatus;
import roomescape.global.exception.ErrorCode;

public enum ReservationErrorCode implements ErrorCode {

    INVALID_NAME(HttpStatus.BAD_REQUEST, "RESERVATION_INVALID_NAME", "예약자 이름을 입력해 주세요."),
    INVALID_DATE(HttpStatus.BAD_REQUEST, "RESERVATION_INVALID_DATE", "예약 날짜 형식은 yyyy-MM-dd이어야 합니다."),
    PAST_RESERVATION(HttpStatus.BAD_REQUEST, "RESERVATION_PAST", "지나간 날짜와 시간은 예약할 수 없습니다."),
    DUPLICATED_RESERVATION(HttpStatus.BAD_REQUEST, "RESERVATION_DUPLICATED", "이미 예약된 시간입니다."),
    RESERVED_PAST(HttpStatus.BAD_REQUEST, "RESERVATION_ALREADY_PAST", "이미 지난 예약은 변경하거나 취소할 수 없습니다."),
    OWNER_MISMATCH(HttpStatus.FORBIDDEN, "RESERVATION_OWNER_MISMATCH", "본인의 예약만 변경하거나 취소할 수 있습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", "예약을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ReservationErrorCode(
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
