package tidify.tidify.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tidify.tidify.dto.LabelResponse;
import tidify.tidify.service.LabelService;

@RestController
@RequiredArgsConstructor
@RequestMapping("app/label")
public class LabelController {
    private final LabelService labelService;

    @GetMapping
    private ResponseEntity<List<LabelResponse>> getLabel() {
        List<LabelResponse> label = labelService.getAllLabel();
        return ResponseEntity.ok().body(label);
    }
}
