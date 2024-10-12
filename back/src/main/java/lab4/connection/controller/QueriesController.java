package lab4.connection.controller;

import lab4.database.entity.GroupByTotalBoxOffice;
import lab4.database.entity.Movie;
import lab4.database.entity.enums.MovieGenre;
import lab4.service.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/queries")
@AllArgsConstructor
public class QueriesController {
    private MovieService movieService;
    private WebSocketController webSocketController;

    @GetMapping("/minTotalBoxOffice")
    public ResponseEntity<Long> minTotalBoxOffice() {
        return ResponseEntity.ok(movieService.giveMinTotalBoxOffice());
    }

    @GetMapping("/groupByTotalBoxOffice")
    public ResponseEntity<List<GroupByTotalBoxOffice>> groupByTotalBoxOffice() {
        return ResponseEntity.ok(movieService.groupByTotalBoxOffice());
    }

    @PostMapping("/zeroOscarCountByGenre")
    public ResponseEntity<Void> zeroOscarCountByGenre(@RequestParam(value = "genre") MovieGenre genre) {
        movieService.zeroOscarCountByGenre(genre);
        webSocketController.update("");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/allWithNoOscars")
    public ResponseEntity<List<Movie>> getAllWithNoOscars() {
        return ResponseEntity.ok(movieService.getAllWithNoOscars());
    }
}
