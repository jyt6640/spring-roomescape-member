package roomescape.reservations.application.dto;

import java.time.LocalTime;

public record ReservationTimeCreateCommand(
        LocalTime startAt
) {
    public static ReservationTimeCreateCommand create(LocalTime startAt) {
        return new ReservationTimeCreateCommand(startAt);
    }
}
