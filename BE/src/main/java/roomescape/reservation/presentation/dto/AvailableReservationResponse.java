package roomescape.reservation.presentation.dto;

import roomescape.reservation.application.dto.ReservationAvailableResult;

public record AvailableReservationResponse(
        String date,
        Long timeId,
        Long themeId,
        boolean available
) {
    public static AvailableReservationResponse createResponse(ReservationAvailableResult reservationAvailableResult) {
        return new AvailableReservationResponse(
                reservationAvailableResult.date(),
                reservationAvailableResult.timeId(),
                reservationAvailableResult.themeId(),
                reservationAvailableResult.available()
        );
    }
}
