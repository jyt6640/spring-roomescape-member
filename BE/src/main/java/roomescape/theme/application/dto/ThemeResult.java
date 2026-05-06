package roomescape.theme.application.dto;

import roomescape.theme.entity.Theme;

public record ThemeResult(
        Long id,
        String name,
        String description,
        String thumbnail
) {
    public static ThemeResult create(Theme theme) {
        return new ThemeResult(
                theme.id(),
                theme.name(),
                theme.description(),
                theme.thumbnail()
        );
    }
}
