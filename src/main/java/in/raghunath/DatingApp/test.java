package in.raghunath.DatingApp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

public class test {

    @RestController
    public static class HelloWorldController {

        @GetMapping("/hello")
        public String hello() {
            return "Hello, World!";
        }
    }
}
