package roomescape.reservation.entity;

import java.time.LocalDate;

public record AvailableReservation(
        LocalDate date,
        Long timeId,
        Long themeId,
        boolean available
) {
}
