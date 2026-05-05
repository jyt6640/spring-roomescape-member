package roomescape.reservation.presentation;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.application.ReservationService;
import roomescape.reservation.application.dto.ReservationCreateCommand;
import roomescape.reservation.application.dto.ReservationResult;
import roomescape.reservation.presentation.dto.ReservationRequest;
import roomescape.reservation.presentation.dto.ReservationResponse;

@RestController
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveReservation(
            @RequestBody ReservationRequest request
    ) {
        ReservationResult response = service.saveReservation(
                new ReservationCreateCommand(
                        request.name(),
                        LocalDate.parse(request.date()),
                        request.timeId(),
                        request.themeId()
                )
        );
        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(ReservationResponse.createResponse(response));
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservations() {
        List<ReservationResult> reservationResults = service.getReservations();
        return ResponseEntity.ok(reservationResults.stream()
                .map(ReservationResponse::createResponse)
                .toList()
        );
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable Long id
    ) {
        service.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
