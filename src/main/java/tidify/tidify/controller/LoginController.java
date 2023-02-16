package tidify.tidify.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login")
public class LoginController {
    @GetMapping
    public ResponseEntity<String> healthCheck() {
        // return ResponseEntity.ok(HttpStatus.OK);
        System.out.println("this is login co");
        return ResponseEntity.ok().body("Nexters TDFY 레쓰고");
    }

}
