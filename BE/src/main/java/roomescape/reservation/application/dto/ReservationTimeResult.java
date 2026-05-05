package roomescape.reservation.application.dto;

import roomescape.reservation.domain.ReservationTime;

public record ReservationTimeResult(
        Long id,
        String startAt
) {
    public static ReservationTimeResult createWithId(ReservationTime reservationTime) {
        return new ReservationTimeResult(
                reservationTime.id(),
                reservationTime.startAt().toString()
        );
    }

    public static ReservationTimeResult createWithNullId(String startAt) {
        return new ReservationTimeResult(null, startAt);
    }
}
