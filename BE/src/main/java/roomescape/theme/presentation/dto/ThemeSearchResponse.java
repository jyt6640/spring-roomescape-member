package roomescape.theme.presentation.dto;

import roomescape.theme.application.dto.ThemeSearchResult;

public record ThemeSearchResponse(
        Long id,
        String name
) {
    public static ThemeSearchResponse createResponse(ThemeSearchResult themeSearchResult) {
        return new ThemeSearchResponse(
                themeSearchResult.id(),
                themeSearchResult.name()
        );
    }
}
