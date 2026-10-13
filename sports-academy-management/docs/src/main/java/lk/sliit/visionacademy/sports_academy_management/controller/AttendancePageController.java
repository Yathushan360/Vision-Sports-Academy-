package lk.sliit.visionacademy.sports_academy_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AttendancePageController {

    @GetMapping("/attendance")
    public String attendancePage() {
        return "attendance";
    }
}