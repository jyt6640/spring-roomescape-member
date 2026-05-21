package roomescape.dto;

public class ThemeCreateRequest {

    private String name;
    private String description;
    private String thumbnailUrl;

    public ThemeCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
