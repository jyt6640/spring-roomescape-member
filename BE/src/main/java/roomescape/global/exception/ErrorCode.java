package roomescape.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // Reservation
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약을 찾을 수 없습니다."),
    RESERVATION_REQUEST_NULL(HttpStatus.BAD_REQUEST, "예약 요청 데이터가 비어있습니다."),

    // ReservationTime
    RESERVATION_TIME_NOT_FOUND(HttpStatus.NOT_FOUND, "예약 시간을 찾을 수 없습니다."),
    RESERVATION_TIME_REQUEST_NULL(HttpStatus.BAD_REQUEST, "예약 시간 요청 데이터가 비어있습니다."),
    RESERVATION_TIME_ALREADY_USED(HttpStatus.BAD_REQUEST, "참조하고 있는 예약 시간이어서 삭제할 수 없습니다."),

    // theme
    THEME_NOT_FOUND(HttpStatus.NOT_FOUND, "테마를 찾을 수 없습니다."),
    THEME_REQUEST_NULL(HttpStatus.BAD_REQUEST, "테마 요청 데이터가 비어있습니다."),
    THEME_ALREADY_USED(HttpStatus.BAD_REQUEST, "참조하고 있는 테마여서 삭제할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
