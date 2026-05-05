package roomescape.reservation.application.dto;

import java.time.LocalDate;
import roomescape.reservation.infrastructure.entity.AvailableReservationTimeEntity;

public record ReservationAvailableResult(
        String date,
        Long timeId,
        Long themeId,
        boolean available
) {
    public static ReservationAvailableResult create(AvailableReservationTimeEntity availableReservationTimeEntity) {
        return new ReservationAvailableResult(
                availableReservationTimeEntity.date().toString(),
                availableReservationTimeEntity.timeId(),
                availableReservationTimeEntity.themeId(),
                availableReservationTimeEntity.available()
        );
    }
}
