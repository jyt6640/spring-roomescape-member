package roomescape.reservation.application.dto;

public record ReservationAvailableCreateCommand(
        String date,
        Long themeId
) {
    public static ReservationAvailableCreateCommand create(String date, Long themeId) {
        return new ReservationAvailableCreateCommand(date, themeId);
    }
}
