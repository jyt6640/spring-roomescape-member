package roomescape.reservation.presentation;

import java.net.URI;
import java.time.LocalTime;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.application.ReservationTimeService;
import roomescape.reservation.application.dto.ReservationTimeCreateCommand;
import roomescape.reservation.application.dto.ReservationTimeResult;
import roomescape.reservation.presentation.dto.ReservationTimeRequest;
import roomescape.reservation.presentation.dto.ReservationTimeResponse;

@RestController
public class ReservationTimeController {

    private final ReservationTimeService service;

    public ReservationTimeController(ReservationTimeService service) {
        this.service = service;
    }

    @PostMapping("/times")
    public ResponseEntity<ReservationTimeResponse> saveTime(
            @RequestBody ReservationTimeRequest request
    ) {
        ReservationTimeResult response = service.saveTime(
                ReservationTimeCreateCommand.create(
                        LocalTime.parse(request.startAt()))
                );
        return ResponseEntity.created(URI.create("/times/" + response.id()))
                .body(ReservationTimeResponse.createResponse(response));
    }

    @GetMapping("/times")
    public ResponseEntity<List<ReservationTimeResponse>> getTimes() {
        List<ReservationTimeResult> response = service.getTimes();
        return ResponseEntity.ok(response.stream()
                .map(ReservationTimeResponse::createResponse)
                .toList()
        );
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteTime(
            @PathVariable Long id
    ) {
        service.deleteTime(id);
        return ResponseEntity.noContent().build();
    }
}
