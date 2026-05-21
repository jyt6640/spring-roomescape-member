package roomescape.reservation.presentation;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.application.ReservationService;
import roomescape.reservation.application.dto.ChangeReservationCommand;
import roomescape.reservation.application.dto.CreateReservationCommand;

@RestController
public class UserReservationController {

    private final ReservationService reservationService;

    public UserReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/users/reservations")
    public ReservationResponse create(@RequestBody ReservationRequest request) {
        CreateReservationCommand command = toCommand(request);
        return ReservationResponse.from(reservationService.createUserReservation(command));
    }

    @GetMapping("/users/reservations")
    public List<ReservationResponse> findMine(@RequestParam String name) {
        return reservationService.findByName(name)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @PatchMapping("/users/reservations/{id}")
    public ReservationResponse change(
            @PathVariable Long id,
            @RequestBody ChangeReservationRequest request
    ) {
        ChangeReservationCommand command = new ChangeReservationCommand(
                id,
                request.name(),
                request.date(),
                request.timeId()
        );
        return ReservationResponse.from(reservationService.changeUserReservation(command));
    }

    @DeleteMapping("/users/reservations/{id}")
    public void delete(
            @PathVariable Long id,
            @RequestParam String name
    ) {
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
