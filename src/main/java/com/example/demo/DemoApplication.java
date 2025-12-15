
package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Main application + domain + repositories + service.
 */
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    // Ensure indexes and seed sample data
    @Bean
    CommandLineRunner ensureIndexesAndSeed(
            MongoTemplate mongoTemplate,
            StaffRepository staffRepository
    ) {
        return args -> {
            mongoTemplate.indexOps(Staff.class)
                    .ensureIndex(new Index().on("employeeId", Sort.Direction.ASC).unique());

            mongoTemplate.indexOps(Attendance.class)
                    .ensureIndex(new Index().on("employeeId", Sort.Direction.ASC));

            mongoTemplate.indexOps(Attendance.class)
                    .ensureIndex(new Index().on("date", Sort.Direction.ASC));

            if (staffRepository.findByEmployeeId("EMP001").isEmpty()) {
                Staff s = new Staff(null, "John Doe", "EMP001", "Sales", null);
                staffRepository.save(s);
            }
        };
    }
}

/* ===================== DOMAIN ===================== */

@Document(collection = "staff")
class Staff {

    @Id
    private String id;
    private String name;
    private String employeeId;
    private String designation;
    private String photoBase64;

    public Staff() {}

    public Staff(String id, String name, String employeeId,
                 String designation, String photoBase64) {
        this.id = id;
        this.name = name;
        this.employeeId = employeeId;
        this.designation = designation;
        this.photoBase64 = photoBase64;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getPhotoBase64() { return photoBase64; }
    public void setPhotoBase64(String photoBase64) { this.photoBase64 = photoBase64; }
}

@Document(collection = "attendance")
class Attendance {

    @Id
    private String id;
    private String employeeId;
    private LocalDate date;
    private LocalDateTime signInTime;
    private LocalDateTime signOutTime;

    public Attendance() {}

    public Attendance(String employeeId, LocalDate date) {
        this.employeeId = employeeId;
        this.date = date;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDateTime getSignInTime() { return signInTime; }
    public void setSignInTime(LocalDateTime signInTime) { this.signInTime = signInTime; }

    public LocalDateTime getSignOutTime() { return signOutTime; }
    public void setSignOutTime(LocalDateTime signOutTime) { this.signOutTime = signOutTime; }
}

/* ===================== REPOSITORIES ===================== */

interface StaffRepository extends MongoRepository<Staff, String> {
    Optional<Staff> findByEmployeeId(String employeeId);
}

interface AttendanceRepository extends MongoRepository<Attendance, String> {
    Optional<Attendance> findByEmployeeIdAndDate(String employeeId, LocalDate date);
    List<Attendance> findAllByDate(LocalDate date);
}

/* ===================== SERVICE ===================== */

@Service
class AttendanceService {

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
