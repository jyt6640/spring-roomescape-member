package roomescape.theme.domain;

public record theme(
        Long id,
        String name,
        String description,
        String thumbnail
) {
    public static theme createWithNullId(String name, String description, String thumbnail) {
        return new theme(null, name, description, thumbnail);
    }

    public static theme createWithId(Long id, String name, String description, String thumbnail) {
        return new theme(id, name, description, thumbnail);
    }
}
