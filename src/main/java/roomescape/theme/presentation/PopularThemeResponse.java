package roomescape.theme.presentation;

import roomescape.theme.domain.PopularTheme;

public record PopularThemeResponse(
        Long id,
        String name,
        String description,
        String thumbnail,
        long reservationCount
) {

    public static PopularThemeResponse from(PopularTheme popularTheme) {
        return new PopularThemeResponse(
                popularTheme.id(),
                popularTheme.name(),
                popularTheme.description(),
                popularTheme.thumbnail(),
                popularTheme.reservationCount()
        );
    }
}
