package com.PeerNest.PeerNest.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/student/test")
    public String studentTest(Authentication authentication) {

        return "Hello Student: "
                + authentication.getName();
    }

    @GetMapping("/instructor/test")
    public String instructorTest(Authentication authentication) {

        return "Hello Instructor: "
                + authentication.getName();
    }

    @GetMapping("/admin/test")
    public String adminTest(Authentication authentication) {

        return "Hello Admin: "
                + authentication.getName();
    }
}