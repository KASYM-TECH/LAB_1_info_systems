package lab4.connection.controller;

import jakarta.servlet.http.HttpServletRequest;
import lab4.database.entity.Location;
import lab4.service.LocationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/locations")
@AllArgsConstructor
public class LocationController {
    private LocationService locationsService;
    private WebSocketController webSocketController;

    @GetMapping()
    public ResponseEntity<List<Location>> getAll(@RequestParam(value = "page", defaultValue = "0") int pageNumber,
                                                    @RequestParam(value = "sortBy", defaultValue = "id") String sortField,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                 @RequestParam(value = "filterName", defaultValue = "") String filterName) {
        List<Location> locations = locationsService.getAll(pageNumber, sortField, pageSize, filterName);
        return ResponseEntity.ok(locations);
    }

    @PostMapping()
    public ResponseEntity<Location> create(@RequestBody Location location) {
        var created = ResponseEntity.ok(locationsService.createLocation(location));
        webSocketController.update("");
        return created;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> get(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(locationsService.getLocationById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location> update(@PathVariable Long id, @RequestBody Location location, HttpServletRequest request) throws Exception {
        var updated = ResponseEntity.ok(locationsService.updateLocation(location.getId(), location,  request.getHeader("Authorization")));
        location.setId(id);
        webSocketController.update("");
        return updated;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(name = "replaceId") Long idToReplace) {
        locationsService.replaceWith(id, idToReplace);
        webSocketController.update("");
        return ResponseEntity.noContent().build();
    }
}

