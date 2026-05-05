package roomescape.reservation.application.dto;

import roomescape.reservation.domain.ReservationTime;

public record ReservationTimeResult(
        Long id,
        String startAt
) {
    public static ReservationTimeResult create(ReservationTime reservationTime) {
        return new ReservationTimeResult(
                reservationTime.id(),
                reservationTime.startAt().toString()
        );
    }
}
