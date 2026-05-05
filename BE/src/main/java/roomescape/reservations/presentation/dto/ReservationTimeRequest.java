package roomescape.reservations.presentation.dto;

import java.time.LocalTime;
import roomescape.global.exception.ErrorCode;
import roomescape.global.exception.customException.ReservationTimeException;

public record ReservationTimeRequest(
        String startAt
) {
    public ReservationTimeRequest {
        validatStartAtNotEmpty(startAt);
    }

    private void validatStartAtNotEmpty(String startAt) {
        if (startAt == null) {
            throw new ReservationTimeException(ErrorCode.RESERVATION_TIME_START_AT_NULL);
        }
    }
}
