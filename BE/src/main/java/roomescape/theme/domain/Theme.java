package roomescape.theme.domain;

public record Theme(
        Long id,
        String name,
        String description,
        String thumbnail
) {
    public static Theme createWithNullId(String name, String description, String thumbnail) {
        return new Theme(null, name, description, thumbnail);
    }

    public static Theme createWithId(Long id, String name, String description, String thumbnail) {
        return new Theme(id, name, description, thumbnail);
    }

    public static Theme findTheme(Long id) {
        return new Theme(
                id,
                null,
                null,
                null
        );
    }
}
