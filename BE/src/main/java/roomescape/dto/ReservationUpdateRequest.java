package roomescape.dto;

public class ReservationUpdateRequest {

    private String date;
    private Long timeId;

    public ReservationUpdateRequest() {
    }

    public String getDate() {
        return date;
    }

    public Long getTimeId() {
        return timeId;
    }
}
