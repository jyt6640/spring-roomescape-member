package roomescape.controller;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.dto.ReservationCreateRequest;
import roomescape.dto.ReservationUpdateRequest;
import roomescape.service.ReservationService;

@RestController
public class UserReservationController {

    private final ReservationService reservationService;

    public UserReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/api/reservation-times/available")
    public List<ReservationTime> findAvailableTimes(@RequestParam String date, @RequestParam Long themeId) {
        return reservationService.findAvailableTimes(date, themeId);
    }

    @GetMapping("/api/reservations")
    public List<Reservation> findMyReservations(@RequestParam String name) {
        return reservationService.findByName(name);
    }

    @PostMapping("/api/reservations")
    public Reservation createReservation(@RequestBody ReservationCreateRequest request) {
        return reservationService.createByUser(request);
    }

    @PatchMapping("/api/reservations/{id}")
    public Reservation updateReservation(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestBody ReservationUpdateRequest request
    ) {
        return reservationService.updateByUser(id, name, request);
    }

    @DeleteMapping("/api/reservations/{id}")
    public void deleteReservation(@PathVariable Long id, @RequestParam String name) {
        reservationService.deleteByUser(id, name);
    }
}
