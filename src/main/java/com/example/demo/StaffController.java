package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "*")
public class StaffController {

    private final StaffRepository staffRepository;

    public StaffController(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @PostMapping
    public ResponseEntity<?> addStaff(@RequestBody Staff staff) {
        if (staff.getEmployeeId() == null || staff.getEmployeeId().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Employee ID is required");
        }

        if (staffRepository.findByEmployeeId(staff.getEmployeeId()).isPresent()) {
            return ResponseEntity.badRequest().body("Employee ID already exists");
        }

        Staff saved = staffRepository.save(staff);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<?> getStaff(@PathVariable String employeeId) {
        return staffRepository.findByEmployeeId(employeeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> getAllStaff() {
        return ResponseEntity.ok(staffRepository.findAll());
    }
}
