package roomescape.reservation.application.dto;

import roomescape.reservation.entity.Reservation;
import roomescape.theme.application.dto.ThemeResult;

public record ReservationResult(
        Long id,
        String name,
        String date,
        ReservationTimeResult time,
        ThemeResult theme
) {
    public static ReservationResult create(Reservation reservation) {
        return new ReservationResult(
                reservation.id(),
                reservation.name(),
                reservation.date().toString(),
                ReservationTimeResult.create(reservation.time()),
                ThemeResult.create(reservation.theme())
        );
    }
}
