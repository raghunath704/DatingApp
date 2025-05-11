package in.raghunath.DatingApp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class text {
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

}
