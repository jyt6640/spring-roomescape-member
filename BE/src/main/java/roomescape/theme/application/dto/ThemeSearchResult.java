package roomescape.theme.application.dto;

import roomescape.theme.entity.ThemeSearch;

public record ThemeSearchResult(
        Long id,
        String name
) {
    public static ThemeSearchResult create(ThemeSearch themeSearch) {
        return new ThemeSearchResult(
                themeSearch.id(),
                themeSearch.name()
        );
    }
}
