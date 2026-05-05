package roomescape.reservations.presentation.dto;

import roomescape.reservations.application.dto.ReservationTimeResult;

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
