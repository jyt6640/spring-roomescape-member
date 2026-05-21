package roomescape.reservation.application;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.EntityNotFoundException;
import roomescape.reservation.application.dto.AvailableTimesQuery;
import roomescape.reservation.application.dto.ChangeReservationCommand;
import roomescape.reservation.application.dto.CreateReservationCommand;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationErrorCode;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.theme.application.ThemeService;
import roomescape.theme.domain.Theme;
import roomescape.time.application.ReservationTimeService;
import roomescape.time.domain.ReservationTime;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;
    private final ReservationValidator reservationValidator;
    private final Clock clock;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeService reservationTimeService,
            ThemeService themeService,
            ReservationValidator reservationValidator,
            Clock clock
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
        this.reservationValidator = reservationValidator;
        this.clock = clock;
    }

    public Reservation createAdminReservation(CreateReservationCommand command) {
        ReservationTime time = resolveAdminTime(command);
        Theme theme = resolveAdminTheme(command.themeId());
        Reservation reservation = Reservation.create(command.name(), command.date(), time, theme);
        return reservationRepository.save(reservation);
    }

    public Reservation createUserReservation(CreateReservationCommand command) {
        ReservationTime time = reservationTimeService.getById(command.timeId());
        Theme theme = themeService.getById(command.themeId());
        Reservation reservation = Reservation.create(command.name(), command.date(), time, theme);
        reservation.validateFuture(LocalDateTime.now(clock));
        reservationValidator.validateAvailable(reservation);
        return reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByName(String name) {
        return reservationRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public List<ReservationTime> findAvailableTimes(AvailableTimesQuery query) {
        LocalDate date = reservationValidator.parseDate(query.date());
        themeService.getById(query.themeId());
        List<Long> reservedTimeIds = reservationRepository.findReservedTimeIds(date, query.themeId());
        return reservationTimeService.findAll()
                .stream()
                .filter(time -> !reservedTimeIds.contains(time.getId()))
                .toList();
    }

    public Reservation changeUserReservation(ChangeReservationCommand command) {
        Reservation reservation = getById(command.id());
        reservation.validateOwner(command.name());
        reservation.validateEditable(LocalDateTime.now(clock));
        Reservation changedReservation = changeSchedule(reservation, command);
        changedReservation.validateFuture(LocalDateTime.now(clock));
        reservationValidator.validateAvailableExceptSelf(changedReservation);
        reservationRepository.updateSchedule(changedReservation);
        return getById(command.id());
    }

    public void deleteAdminReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public void deleteUserReservation(
            Long id,
            String name
    ) {
        Reservation reservation = getById(id);
        reservation.validateOwner(name);
        reservation.validateEditable(LocalDateTime.now(clock));
        reservationRepository.deleteById(id);
    }

    private Reservation getById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ReservationErrorCode.NOT_FOUND));
    }

    private Reservation changeSchedule(
            Reservation reservation,
            ChangeReservationCommand command
    ) {
        ReservationTime time = reservationTimeService.getById(command.timeId());
        return reservation.rescheduled(command.date(), time);
    }

    private ReservationTime resolveAdminTime(CreateReservationCommand command) {
        if (command.timeId() != null) {
            return reservationTimeService.getOrCreateDefault(command.timeId());
        }
        return reservationTimeService.getOrCreateByStartAt(command.time());
    }

    private Theme resolveAdminTheme(Long themeId) {
        if (themeId != null) {
            return themeService.getById(themeId);
        }
        return themeService.getOrCreateDefaultTheme();
    }
}
