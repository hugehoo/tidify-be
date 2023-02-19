package tidify.tidify.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;

@Api(tags = "TIDIFY 레쓰고")
@RestController
@RequestMapping("api")
public class BasicController {

    @Operation(summary="Health Check")
    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok().body("Nexters TDFY 레쓰고");
    }

}
