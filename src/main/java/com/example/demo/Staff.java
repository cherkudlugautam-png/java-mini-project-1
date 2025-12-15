package com.example.demo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "staff")
public class Staff {

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
