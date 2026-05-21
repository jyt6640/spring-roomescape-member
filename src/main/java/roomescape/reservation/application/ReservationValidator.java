package roomescape.reservation.application;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.springframework.stereotype.Component;
import roomescape.global.exception.BusinessException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationErrorCode;
import roomescape.reservation.domain.ReservationRepository;

@Component
public class ReservationValidator {

    private final ReservationRepository reservationRepository;

    public ReservationValidator(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public void validateAvailable(Reservation reservation) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeId(
                reservation.date(),
                reservation.getTime().getId(),
                reservation.getTheme().getId()
        )) {
            throw new BusinessException(ReservationErrorCode.DUPLICATED_RESERVATION);
        }
    }

    public void validateAvailableExceptSelf(Reservation reservation) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeIdExceptId(
                reservation.date(),
                reservation.getTime().getId(),
                reservation.getTheme().getId(),
                reservation.getId()
        )) {
            throw new BusinessException(ReservationErrorCode.DUPLICATED_RESERVATION);
        }
    }

    public LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException exception) {
            throw new BusinessException(ReservationErrorCode.INVALID_DATE);
        }
    }
}
