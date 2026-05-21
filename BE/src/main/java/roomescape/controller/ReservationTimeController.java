package roomescape.controller;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.ReservationTime;
import roomescape.dto.ReservationTimeCreateRequest;
import roomescape.service.ReservationTimeService;

@RestController
public class ReservationTimeController {

    private final ReservationTimeService timeService;

    public ReservationTimeController(ReservationTimeService timeService) {
        this.timeService = timeService;
    }

    @GetMapping("/times")
    public List<ReservationTime> findTimes() {
        return timeService.findAll();
    }

    @PostMapping("/times")
    public ReservationTime createTime(@RequestBody ReservationTimeCreateRequest request) {
        return timeService.create(request.getStartAt());
    }

    @DeleteMapping("/times/{id}")
    public void deleteTime(@PathVariable Long id) {
        timeService.delete(id);
    }
}
