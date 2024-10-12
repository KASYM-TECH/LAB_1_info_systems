package lab4.connection.controller;

import jakarta.servlet.http.HttpServletRequest;
import lab4.database.entity.Coordinates;
import lab4.service.CoordinatesService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/coordinates")
@AllArgsConstructor
public class CoordinatesController {
    private CoordinatesService coordinatesService;
    private WebSocketController webSocketController;

    @GetMapping()
    public ResponseEntity<List<Coordinates>> getAll(@RequestParam(value = "page", defaultValue = "0") int pageNumber,
                                                    @RequestParam(value = "sortBy", defaultValue = "id") String sortField,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        List<Coordinates> coordinates = coordinatesService.getAll(pageNumber, sortField, pageSize);
        return ResponseEntity.ok(coordinates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coordinates> get(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(coordinatesService.getCoordinatesById(id));
    }

    @PostMapping()
    public ResponseEntity<Coordinates> create(@RequestBody Coordinates coordinates) {
        var coordinatesCreated = ResponseEntity.ok(coordinatesService.createCoordinates(coordinates));
        webSocketController.update("");
        return coordinatesCreated;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coordinates> update(@PathVariable Long id, @RequestBody Coordinates coordinates, HttpServletRequest request) throws Exception {
        coordinates.setId(id);
        var coordinatesUpdated = ResponseEntity.ok(coordinatesService.updateCoordinates(coordinates.getId(), coordinates, request.getHeader("Authorization")));
        webSocketController.update("");
        return coordinatesUpdated;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(name = "replaceId") Long idToReplace) throws Exception {
        coordinatesService.replaceCoordinates(id, idToReplace);
        webSocketController.update("");
        return ResponseEntity.noContent().build();
    }
}
