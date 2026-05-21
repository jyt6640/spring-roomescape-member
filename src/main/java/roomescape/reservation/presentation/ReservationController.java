package roomescape.reservation.presentation;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.application.ReservationService;
import roomescape.reservation.application.dto.AvailableTimesQuery;
import roomescape.reservation.application.dto.CreateReservationCommand;
import roomescape.time.presentation.ReservationTimeResponse;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> findAll() {
        return reservationService.findAll()
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @PostMapping("/reservations")
    public ReservationResponse create(@RequestBody ReservationRequest request) {
        CreateReservationCommand command = toCommand(request);
        return ReservationResponse.from(reservationService.createAdminReservation(command));
    }

    @GetMapping("/reservations/available-times")
    public List<ReservationTimeResponse> findAvailableTimes(
            @RequestParam String date,
            @RequestParam Long themeId
    ) {
        return reservationService.findAvailableTimes(new AvailableTimesQuery(date, themeId))
                .stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }

    @DeleteMapping("/reservations/{id}")
    public void delete(
            @PathVariable Long id,
            @RequestParam(required = false) String name
    ) {
        if (name == null || name.isBlank()) {
            reservationService.deleteAdminReservation(id);
            return;
        }
        reservationService.deleteUserReservation(id, name);
    }

    private CreateReservationCommand toCommand(ReservationRequest request) {
        return new CreateReservationCommand(
                request.name(),
                request.date(),
                request.timeId(),
                request.time(),
                request.themeId()
        );
    }
}
