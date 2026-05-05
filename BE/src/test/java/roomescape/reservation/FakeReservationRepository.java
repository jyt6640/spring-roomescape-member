package roomescape.reservation;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.ReservationTimeRepository;
import roomescape.reservation.domain.AvailableReservation;

public class FakeReservationRepository implements ReservationRepository {

    private final Map<Long, Reservation> store = new HashMap<>();
    private final ReservationTimeRepository reservationTimeRepository;
    private Long sequence = 0L;

    public FakeReservationRepository(ReservationTimeRepository reservationTimeRepository) {
        this.reservationTimeRepository = reservationTimeRepository;
    }

    @Override
    public Reservation save(Reservation reservation) {
        if (reservation.id() == null) {
            Reservation saved = Reservation.createWithId(
                    sequence++,
                    reservation.name(),
                    reservation.date(),
                    reservation.time(),
                    reservation.theme()
            );
            store.put(saved.id(), saved);
            return saved;
        }

        store.put(reservation.id(), reservation);
        return reservation;
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Reservation> findAll() {
        return store.values().stream().toList();
    }

    @Override
    public boolean existsByReservationTimeId(Long reservationTimeId) {
        return store.values().stream()
                .anyMatch(reservation -> reservation.time()
                        .id()
                        .equals(reservationTimeId));
    }

    @Override
    public boolean existsByThemeId(Long themeId) {
        return store.values().stream()
                .anyMatch(reservation -> reservation.theme()
                        .id()
                        .equals(themeId));
    }

    @Override
    public List<AvailableReservation> findAvailableAllTime(LocalDate date, Long themeId) {
        return reservationTimeRepository.findAll().stream()
                .map(time -> new AvailableReservation(
                        date,
                        time.id(),
                        themeId,
                        isAvailable(date, themeId, time.id())
                ))
                .toList();
    }

    private boolean isAvailable(LocalDate date, Long themeId, Long timeId) {
        return store.values().stream()
                .noneMatch(reservation ->
                        reservation.date().equals(date)
                                && reservation.theme().id().equals(themeId)
                                && reservation.time().id().equals(timeId)
                );
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}
