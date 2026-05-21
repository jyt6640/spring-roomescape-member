package roomescape.reservation.presentation;

public record ChangeReservationRequest(
        String name,
        String date,
        Long timeId
) {
}
