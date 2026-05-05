package roomescape.theme.application.dto;

public record ThemeResult(
        Long id,
        String name,
        String description,
        String thumbnail
) {
    public static ThemeResult create(Long id, String name, String description, String thumbnail) {
        return new ThemeResult(id, name, description, thumbnail);
    }
}
