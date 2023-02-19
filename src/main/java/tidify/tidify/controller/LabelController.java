package tidify.tidify.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import tidify.tidify.dto.LabelResponse;
import tidify.tidify.service.LabelService;

@Api(tags = "라벨")
@RestController
@RequiredArgsConstructor
@RequestMapping("app/label")
public class LabelController {
    private final LabelService labelService;

    @Operation(summary="라벨 조회", description="라벨 선택시, 전체 라벨 조회")
    @GetMapping
    private ResponseEntity<List<LabelResponse>> getLabel() {
        List<LabelResponse> label = labelService.getAllLabel();
        return ResponseEntity.ok().body(label);
    }
}
