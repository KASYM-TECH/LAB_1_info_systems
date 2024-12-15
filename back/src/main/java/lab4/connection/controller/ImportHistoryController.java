package lab4.connection.controller;

import jakarta.servlet.http.HttpServletRequest;
import lab4.database.entity.Coordinates;
import lab4.database.entity.ImportHistory;
import lab4.service.ImportHistoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/import-history")
@AllArgsConstructor
public class ImportHistoryController {
    private ImportHistoryService importHistoryService;
    private WebSocketController webSocketController;

    @GetMapping()
    public ResponseEntity<List<ImportHistory>> getAll(@RequestParam(value = "page", defaultValue = "0") int pageNumber,
                                                      @RequestParam(value = "sortBy", defaultValue = "id") String sortField,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpServletRequest request
                                                 ) {
        List<ImportHistory> ImportHistory = importHistoryService.getAll(pageNumber, sortField, pageSize, request.getHeader("Authorization"));
        return ResponseEntity.ok(ImportHistory);
    }

    @PostMapping()
    public ResponseEntity<ImportHistory> create(@RequestParam("file") MultipartFile file, @RequestParam(value = "entity", defaultValue = "0") String entityType, HttpServletRequest request) {
        var created = ResponseEntity.ok(importHistoryService.createImportHistory(request.getHeader("Authorization"), entityType, file));
        webSocketController.update("");
        return created;
    }
}
