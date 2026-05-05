package roomescape.reservation.presentation.dto;

import roomescape.global.exception.ErrorCode;
import roomescape.global.exception.ReservationTimeException;

public record ReservationTimeRequest(
        String startAt
) {
    public ReservationTimeRequest {
        validatStartAtNotEmpty(startAt);
    }

    private void validatStartAtNotEmpty(String startAt) {
        if (startAt == null) {
            throw new ReservationTimeException(ErrorCode.RESERVATION_TIME_REQUEST_NULL);
        }
    }
}
