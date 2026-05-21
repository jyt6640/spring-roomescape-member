package roomescape.dto;

public class ReservationAvailableRequest {

    private String date;
    private Long themeId;

    public ReservationAvailableRequest() {
    }

    public String getDate() {
        return date;
    }

    public Long getThemeId() {
        return themeId;
    }
}
