package com.example.gym.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = status != null ? Integer.parseInt(status.toString()) : 500;

        String errorTitle = "Error";
        String errorMessage = "An unexpected error occurred.";

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            errorTitle = "Page Not Found";
            errorMessage = "The page you're looking for doesn't exist.";
        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            errorTitle = "Access Denied";
            errorMessage = "You don't have permission to access this resource.";
        } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            errorTitle = "Server Error";
            errorMessage = "Something went wrong on our side. Please try again later.";
        }

        model.addAttribute("status", statusCode);
        model.addAttribute("error", errorTitle);
        model.addAttribute("message", errorMessage);

        return "error";
    }
}