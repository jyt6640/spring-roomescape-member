package roomescape.reservation.presentation.dto;

import roomescape.reservation.application.dto.ReservationResult;

public record ReservationResponse(
        Long id,
        String name,
        String date,
        Long timeId,
        Long themeId
) {
    public static ReservationResponse createResponse(ReservationResult reservationResult) {
        return new ReservationResponse(
                reservationResult.id(),
                reservationResult.name(),
                reservationResult.date(),
                reservationResult.time().id(),
                reservationResult.theme().id()
        );
    }
}
