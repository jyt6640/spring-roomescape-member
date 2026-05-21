package roomescape.reservation.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import roomescape.global.exception.BusinessException;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

public class Reservation {

    private final Long id;
    private final String name;
    private final LocalDate date;
    private final ReservationTime time;
    private final Theme theme;

    private Reservation(
            Long id,
            String name,
            LocalDate date,
            ReservationTime time,
            Theme theme
    ) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    public static Reservation create(
            String name,
            String date,
            ReservationTime time,
            Theme theme
    ) {
        validateName(name);
        return new Reservation(null, name, parseDate(date), time, theme);
    }

    public static Reservation restore(
            Long id,
            String name,
            String date,
            ReservationTime time,
            Theme theme
    ) {
        return new Reservation(id, name, LocalDate.parse(date), time, theme);
    }

    public Reservation persisted(Long id) {
        return new Reservation(id, name, date, time, theme);
    }

    public Reservation rescheduled(
            String date,
            ReservationTime time
    ) {
        return new Reservation(id, name, parseDate(date), time, theme);
    }

    public void validateFuture(LocalDateTime now) {
        if (date.atTime(time.startAt()).isBefore(now)) {
            throw new BusinessException(ReservationErrorCode.PAST_RESERVATION);
        }
    }

    public void validateEditable(LocalDateTime now) {
        if (date.atTime(time.startAt()).isBefore(now)) {
            throw new BusinessException(ReservationErrorCode.RESERVED_PAST);
        }
    }

    public void validateOwner(String name) {
        if (!Objects.equals(this.name, name)) {
            throw new BusinessException(ReservationErrorCode.OWNER_MISMATCH);
        }
    }

    public boolean hasSameSchedule(Reservation other) {
        return Objects.equals(date, other.date)
                && time.equals(other.time)
                && theme.equals(other.theme);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date.toString();
    }

    public LocalDate date() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ReservationErrorCode.INVALID_NAME);
        }
    }

    private static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException exception) {
            throw new BusinessException(ReservationErrorCode.INVALID_DATE);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Reservation other)) {
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
