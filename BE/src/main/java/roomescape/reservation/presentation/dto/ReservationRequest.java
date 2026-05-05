package roomescape.reservation.presentation.dto;

import roomescape.global.exception.ErrorCode;
import roomescape.global.exception.ReservationException;
import roomescape.global.exception.ReservationTimeException;

public record ReservationRequest(
        String name,
        String date,
        Long timeId
) {
    public ReservationRequest{
        validateNameNotEmpty(name);
        validateDateNotEmpty(date);
        validateTimeIdNotEmpty(timeId);
    }

    private static void validateNameNotEmpty(String name) {
        if (name == null || name.trim().isBlank()) {
            throw new ReservationException(ErrorCode.RESERVATION_REQUEST_NULL);
        }
    }
    
    private static void validateTimeIdNotEmpty(Long timeId) {
        if (timeId == null) {
            throw new ReservationTimeException(ErrorCode.RESERVATION_REQUEST_NULL);
        }
    }

    private static void validateDateNotEmpty(String date) {
        if (date == null) {
            throw new ReservationException(ErrorCode.RESERVATION_REQUEST_NULL);
        }
    }
}
