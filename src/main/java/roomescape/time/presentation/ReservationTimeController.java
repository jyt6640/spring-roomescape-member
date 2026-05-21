package roomescape.time.presentation;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.time.application.ReservationTimeService;

@RestController
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @PostMapping("/times")
    public ReservationTimeResponse create(@RequestBody ReservationTimeRequest request) {
        return ReservationTimeResponse.from(reservationTimeService.create(request.startAt()));
    }

    @GetMapping("/times")
    public List<ReservationTimeResponse> findAll() {
        return reservationTimeService.findAll()
                .stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }

    @DeleteMapping("/times/{id}")
    public void delete(@PathVariable Long id) {
        reservationTimeService.delete(id);
    }
}
