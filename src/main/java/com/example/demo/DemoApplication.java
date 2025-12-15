package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

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
