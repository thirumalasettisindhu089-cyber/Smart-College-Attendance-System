package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
@RestController
public class App {

    private final AttendanceRepository repository;

    public App(AttendanceRepository repository) {
        this.repository = repository;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @PostMapping("/attendance")
    public Attendance mark(
            @RequestParam String rollNo,
            @RequestParam String status) {

        Attendance record = new Attendance();

        record.setRollNo(rollNo);
        record.setStatus(
            status.equalsIgnoreCase("P") ? "Present" : "Absent"
        );
        record.setDate(LocalDate.now());

        return repository.save(record);
    }

    @GetMapping("/attendance")
    public List<Attendance> all() {
        return repository.findAll();
    }

    @GetMapping("/attendance/percentage/{rollNo}")
    public String percentage(@PathVariable String rollNo) {

        List<Attendance> records = repository.findAll()
                .stream()
                .filter(r -> r.getRollNo().equals(rollNo))
                .toList();

        long total = records.size();

        long present = records.stream()
                .filter(r -> r.getStatus().equals("Present"))
                .count();

        if (total == 0) {
            return "No records found for " + rollNo;
        }

        return rollNo + " attendance: "
                + (present * 100.0 / total) + "%";
    }
}