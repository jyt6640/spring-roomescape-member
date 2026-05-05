package roomescape.theme.domain;

public record ThemeSearch(
        Long id,
        String name
) {
    public static ThemeSearch create(Long id, String name) {
        return new ThemeSearch(id, name);
    }
}
