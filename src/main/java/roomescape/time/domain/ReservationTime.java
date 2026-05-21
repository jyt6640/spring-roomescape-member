package roomescape.time.domain;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import roomescape.global.exception.BusinessException;

public class ReservationTime {

    private final Long id;
    private final LocalTime startAt;

    private ReservationTime(
            Long id,
            LocalTime startAt
    ) {
        this.id = id;
        this.startAt = startAt;
    }

    public static ReservationTime create(String startAt) {
        return new ReservationTime(null, parse(startAt));
    }

    public static ReservationTime restore(
            Long id,
            String startAt
    ) {
        return new ReservationTime(id, LocalTime.parse(startAt));
    }

    public ReservationTime persisted(Long id) {
        return new ReservationTime(id, startAt);
    }

    public boolean hasId(Long id) {
        return Objects.equals(this.id, id);
    }

    public Long getId() {
        return id;
    }

    public String getStartAt() {
        return startAt.toString();
    }

    public LocalTime startAt() {
        return startAt;
    }

    private static LocalTime parse(String startAt) {
        if (startAt == null || startAt.isBlank()) {
            throw new BusinessException(ReservationTimeErrorCode.INVALID_START_AT);
        }
        try {
            return LocalTime.parse(startAt);
        } catch (DateTimeParseException exception) {
            throw new BusinessException(ReservationTimeErrorCode.INVALID_START_AT);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ReservationTime other)) {
            return false;
        }
        if (id == null || other.id == null) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return System.identityHashCode(this);
        }
        return Objects.hash(id);
    }
}
