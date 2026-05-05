package roomescape.theme.presentation.dto;

import roomescape.theme.application.dto.ThemeResult;

public record ThemeResponse(
        Long id,
        String name,
        String description,
        String thumbnail
) {
    public static ThemeResponse createResponse(ThemeResult themeResult) {
        return new ThemeResponse(
                themeResult.id(),
                themeResult.name(),
                themeResult.description(),
                themeResult.thumbnail()
        );
    }
}
