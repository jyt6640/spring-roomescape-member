package roomescape.dto;

public class ReservationCreateRequest {

    private String name;
    private String date;
    private String time;
    private Long timeId;
    private Long themeId;

    public ReservationCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Long getTimeId() {
        return timeId;
    }

    public Long getThemeId() {
        return themeId;
    }
}
