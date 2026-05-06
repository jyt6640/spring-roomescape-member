package roomescape.reservation.application.dto;

import roomescape.reservation.entity.AvailableReservation;

public record ReservationAvailableResult(
        String date,
        Long timeId,
        Long themeId,
        boolean available
) {
    public static ReservationAvailableResult create(AvailableReservation availableReservation) {
        return new ReservationAvailableResult(
                availableReservation.date().toString(),
                availableReservation.timeId(),
                availableReservation.themeId(),
                availableReservation.available()
        );
    }
}
