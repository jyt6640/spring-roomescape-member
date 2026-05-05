package roomescape.reservation.presentation.dto;

import roomescape.reservation.application.dto.ReservationResult;

public record ReservationResponse(
        Long id,
        String name,
        String date,
        ReservationTimeResponse time
) {
    public static ReservationResponse createResponse(ReservationResult reservationResult) {
        return new ReservationResponse(
                reservationResult.id(),
                reservationResult.name(),
                reservationResult.date(),
                ReservationTimeResponse.createResponse(reservationResult.time())
        );
    }
}
