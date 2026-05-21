package roomescape.theme.domain;

public record PopularTheme(
        Long id,
        String name,
        String description,
        String thumbnail,
        long reservationCount
) {
}
