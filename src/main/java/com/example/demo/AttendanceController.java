package com.example.demo;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody Map<String, String> payload) {
        String employeeId = payload.get("employeeId");
        if (employeeId == null || employeeId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Employee ID is required");
        }

        try {
            Attendance attendance = attendanceService.signIn(employeeId);
            Map<String, Object> response = new HashMap<>();
            response.put("employeeId", attendance.getEmployeeId());
            response.put("date", attendance.getDate());
            response.put("timestamp", attendance.getSignInTime());
            response.put("message", "Signed in successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOut(@RequestBody Map<String, String> payload) {
        String employeeId = payload.get("employeeId");
        if (employeeId == null || employeeId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Employee ID is required");
        }

        try {
            Attendance attendance = attendanceService.signOut(employeeId);
            Map<String, Object> response = new HashMap<>();
            response.put("employeeId", attendance.getEmployeeId());
            response.put("date", attendance.getDate());
            response.put("timestamp", attendance.getSignOutTime());
            response.put("message", "Signed out successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAttendanceByDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now();
        List<Attendance> attendances = attendanceService.getByDate(queryDate);
        return ResponseEntity.ok(attendances);
    }
}
