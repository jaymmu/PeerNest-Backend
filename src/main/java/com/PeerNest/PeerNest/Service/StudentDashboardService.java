
package com.PeerNest.PeerNest.Service;

import com.PeerNest.PeerNest.Dto.StudentDashboardResponse;

public interface StudentDashboardService {
    StudentDashboardResponse getDashboard(String email);
}
