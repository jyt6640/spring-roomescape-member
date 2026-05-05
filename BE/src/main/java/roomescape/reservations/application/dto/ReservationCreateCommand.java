package roomescape.reservations.application.dto;

import java.time.LocalDate;

public record ReservationCreateCommand(
        String name,
        LocalDate date,
        Long timeId
) {
    public static ReservationCreateCommand create(String name, LocalDate date, Long timeId) {
        return new ReservationCreateCommand(
                name,
                date,
                timeId
        );
    }
}
