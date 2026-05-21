package roomescape.reservation.application.dto;

public record ChangeReservationCommand(
        Long id,
        String name,
        String date,
        Long timeId
) {
}
