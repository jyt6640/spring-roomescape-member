package roomescape.reservation.application.dto;

public record CreateReservationCommand(
        String name,
        String date,
        Long timeId,
        String time,
        Long themeId
) {
}
