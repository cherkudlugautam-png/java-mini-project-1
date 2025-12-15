package com.example.demo;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StaffRepository staffRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             StaffRepository staffRepository) {
        this.attendanceRepository = attendanceRepository;
        this.staffRepository = staffRepository;
    }

    public Attendance signIn(String employeeId) {
        staffRepository.findByEmployeeId(employeeId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Staff not found: " + employeeId));

        LocalDate today = LocalDate.now();
        Attendance att = attendanceRepository
                .findByEmployeeIdAndDate(employeeId, today)
                .orElseGet(() -> new Attendance(employeeId, today));

        att.setSignInTime(LocalDateTime.now());
        return attendanceRepository.save(att);
    }

    public Attendance signOut(String employeeId) {
        staffRepository.findByEmployeeId(employeeId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Staff not found: " + employeeId));

        LocalDate today = LocalDate.now();
        Attendance att = attendanceRepository
                .findByEmployeeIdAndDate(employeeId, today)
                .orElseGet(() -> new Attendance(employeeId, today));

        att.setSignOutTime(LocalDateTime.now());
        return attendanceRepository.save(att);
    }

    public List<Attendance> getByDate(LocalDate date) {
        return attendanceRepository.findAllByDate(date);
    }
}
