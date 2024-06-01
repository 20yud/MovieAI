package com.libray.MovieAi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String showDashboardPage() {
        return "admin/dashboard"; // Assuming your HTML file is named "dashboard.html" and it's under "admin" directory
    }
}
