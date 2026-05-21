package roomescape.reservation.presentation;

public record ReservationRequest(
        String name,
        String date,
        Long timeId,
        String time,
        Long themeId
) {
}
