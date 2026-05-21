package roomescape.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.dto.ReservationCreateRequest;
import roomescape.dto.ReservationUpdateRequest;
import roomescape.exception.RoomescapeException;
import roomescape.repository.ReservationRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeService timeService;
    private final ThemeService themeService;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeService timeService,
            ThemeService themeService
    ) {
        this.reservationRepository = reservationRepository;
        this.timeService = timeService;
        this.themeService = themeService;
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public List<Reservation> findByName(String name) {
        validateNotBlank(name, "예약자 이름을 입력해야 합니다.");
        return reservationRepository.findByName(name);
    }

    public List<ReservationTime> findAvailableTimes(String date, Long themeId) {
        LocalDate reservationDate = parseDate(date);
        Theme theme = themeService.requireTheme(themeId);
        return timeService.findAll().stream()
                .filter(time -> !reservationRepository.existsByDateTimeTheme(date, time.getId(), theme.getId()))
                .filter(time -> !isPast(reservationDate, time))
                .toList();
    }

    @Transactional
    public Reservation createByAdmin(ReservationCreateRequest request) {
        validateReservationInput(request.getName(), request.getDate());
        ReservationTime time = timeService.getOrCreateLegacyTime(request.getTime(), request.getTimeId());
        Theme theme = themeService.getOrCreateDefaultTheme(request.getThemeId());
        return reservationRepository.save(request.getName(), request.getDate(), time.getId(), theme.getId());
    }

    @Transactional
    public Reservation createByUser(ReservationCreateRequest request) {
        validateReservationInput(request.getName(), request.getDate());
        ReservationTime time = timeService.requireTime(request.getTimeId());
        Theme theme = themeService.requireTheme(request.getThemeId());
        LocalDate date = parseDate(request.getDate());

        if (isPast(date, time)) {
            throw new RoomescapeException(HttpStatus.BAD_REQUEST, "PAST_RESERVATION", "지나간 날짜와 시간에는 예약할 수 없습니다.");
        }
        if (reservationRepository.existsByDateTimeTheme(request.getDate(), time.getId(), theme.getId())) {
            throw new RoomescapeException(HttpStatus.CONFLICT, "DUPLICATED_RESERVATION", "이미 예약된 날짜, 시간, 테마입니다.");
        }
        return reservationRepository.save(request.getName(), request.getDate(), time.getId(), theme.getId());
    }

    @Transactional
    public void deleteByAdmin(Long id) {
        reservationRepository.deleteById(id);
    }

    @Transactional
    public void deleteByUser(Long id, String name) {
        Reservation reservation = requireReservation(id);
        assertOwner(reservation, name);
        if (isPast(parseDate(reservation.getDate()), reservation.getTime())) {
            throw new RoomescapeException(HttpStatus.BAD_REQUEST, "PAST_RESERVATION", "이미 지난 예약은 취소할 수 없습니다.");
        }
        reservationRepository.deleteById(id);
    }

    @Transactional
    public Reservation updateByUser(Long id, String name, ReservationUpdateRequest request) {
        Reservation reservation = requireReservation(id);
        assertOwner(reservation, name);
        validateDate(request.getDate());
        ReservationTime time = timeService.requireTime(request.getTimeId());
        LocalDate date = parseDate(request.getDate());

        if (isPast(parseDate(reservation.getDate()), reservation.getTime())) {
            throw new RoomescapeException(HttpStatus.BAD_REQUEST, "PAST_RESERVATION", "이미 지난 예약은 변경할 수 없습니다.");
        }
        if (isPast(date, time)) {
            throw new RoomescapeException(HttpStatus.BAD_REQUEST, "PAST_RESERVATION", "지나간 날짜와 시간으로 변경할 수 없습니다.");
        }
        if (reservationRepository.existsByDateTimeThemeExcludingId(
                request.getDate(),
                time.getId(),
                reservation.getTheme().getId(),
                id
        )) {
            throw new RoomescapeException(HttpStatus.CONFLICT, "DUPLICATED_RESERVATION", "변경하려는 시간이 이미 예약되었습니다.");
        }
        reservationRepository.updateDateAndTime(id, request.getDate(), time.getId());
        return requireReservation(id);
    }

    private Reservation requireReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", "존재하지 않는 예약입니다."));
    }

    private void assertOwner(Reservation reservation, String name) {
        validateNotBlank(name, "예약자 이름을 입력해야 합니다.");
        if (!reservation.getName().equals(name)) {
            throw new RoomescapeException(HttpStatus.FORBIDDEN, "NOT_RESERVATION_OWNER", "본인의 예약만 변경하거나 취소할 수 있습니다.");
        }
    }

    private void validateReservationInput(String name, String date) {
        validateNotBlank(name, "예약자 이름은 비어 있을 수 없습니다.");
        validateDate(date);
    }

    private void validateDate(String date) {
        parseDate(date);
    }

    private LocalDate parseDate(String date) {
        validateNotBlank(date, "예약 날짜는 비어 있을 수 없습니다.");
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException exception) {
            throw new RoomescapeException(HttpStatus.BAD_REQUEST, "INVALID_DATE", "예약 날짜는 yyyy-MM-dd 형식이어야 합니다.");
        }
    }

    private boolean isPast(LocalDate date, ReservationTime time) {
        LocalDateTime reservationDateTime = LocalDateTime.of(date, LocalTime.parse(time.getStartAt()));
        return reservationDateTime.isBefore(LocalDateTime.now()) || reservationDateTime.isEqual(LocalDateTime.now());
    }

    private void validateNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new RoomescapeException(HttpStatus.BAD_REQUEST, "INVALID_INPUT", message);
        }
    }
}
