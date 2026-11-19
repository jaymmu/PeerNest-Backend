
package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.AdminDashboardResponse;
import com.PeerNest.PeerNest.Service.AdminDashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/statistics")
    public ResponseEntity<AdminDashboardResponse> getDashboardStatistics() {

        return ResponseEntity.ok(
                adminDashboardService.getDashboardStatistics()
        );
    }
}
