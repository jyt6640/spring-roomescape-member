package roomescape.reservation.infrastructure.entity;

import java.time.LocalDate;

public record AvailableReservationTimeEntity(
        LocalDate date,
        Long timeId,
        Long themeId,
        boolean available
) {
}
