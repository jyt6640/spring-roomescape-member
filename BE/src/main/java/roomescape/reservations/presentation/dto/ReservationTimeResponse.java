package roomescape.reservations.presentation.dto;

import roomescape.reservations.entity.ReservationTime;

public record ReservationTimeResponse(
        Long id,
        String startAt
) {
    public static ReservationTimeResponse from(ReservationTime reservationTime) {
        return new ReservationTimeResponse(
                reservationTime.id(),
                reservationTime.startAt().toString()
        );
    }
}
