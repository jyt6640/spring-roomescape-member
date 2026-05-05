package roomescape.reservation.presentation.dto;

import roomescape.reservation.application.dto.ReservationTimeResult;

public record ReservationTimeResponse(
        Long id,
        String startAt
) {
    public static ReservationTimeResponse createResponse(ReservationTimeResult reservationTimeResult) {
        return new ReservationTimeResponse(
                reservationTimeResult.id(),
                reservationTimeResult.startAt()
        );
    }
}
