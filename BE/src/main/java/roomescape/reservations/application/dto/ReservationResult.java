package roomescape.reservations.application.dto;

import roomescape.reservations.domain.Reservation;

public record ReservationResult(
        Long id,
        String name,
        String date,
        ReservationTimeResult time
) {
    public static ReservationResult create(Reservation reservation) {
        return new ReservationResult(
                reservation.id(),
                reservation.name(),
                reservation.date().toString(),
                ReservationTimeResult.createWithId(reservation.time())
        );
    }
}
