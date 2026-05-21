package roomescape.reservation.application.dto;

public record AvailableTimesQuery(
        String date,
        Long themeId
) {
}
