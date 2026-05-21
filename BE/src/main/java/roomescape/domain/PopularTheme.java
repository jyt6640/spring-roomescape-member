package roomescape.domain;

public class PopularTheme {

    private Long id;
    private String name;
    private String description;
    private String thumbnailUrl;
    private int reservationCount;

    public PopularTheme() {
    }

    public PopularTheme(Long id, String name, String description, String thumbnailUrl, int reservationCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.reservationCount = reservationCount;
    }

    public Long getId() {
        return id;
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

    public int getReservationCount() {
        return reservationCount;
    }
}
