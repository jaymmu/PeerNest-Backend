
package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.StudentDashboardResponse;
import com.PeerNest.PeerNest.Service.StudentDashboardService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentDashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<StudentDashboardResponse> getDashboard(
            Authentication authentication) {

        StudentDashboardResponse response =
                dashboardService.getDashboard(authentication.getName());

        return ResponseEntity.ok(response);
    }
}
