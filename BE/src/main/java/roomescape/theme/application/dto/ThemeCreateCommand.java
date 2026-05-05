package roomescape.theme.application.dto;

public record ThemeCreateCommand(
        String name,
        String description,
        String thumbnail
) {
    public static ThemeCreateCommand create(String name, String description, String thumbnail) {
        return new ThemeCreateCommand(name, description, thumbnail);
    }
}
