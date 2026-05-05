package roomescape.reservation.application.dto;

import java.time.LocalDate;

public record ReservationCreateCommand(
        String name,
        LocalDate date,
        Long timeId,
        Long themeId
) {
    public static ReservationCreateCommand create(String name, LocalDate date, Long timeId, Long themeId) {
        return new ReservationCreateCommand(
                name,
                date,
                timeId,
                themeId
        );
    }
}
