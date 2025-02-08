package com.hitesh.loan_application_system.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.hitesh.loan_application_system.model.LoanApplication;
import com.hitesh.loan_application_system.model.LoanApplication.Status;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/api/loan")
public class LoanApplicationController {
    
    private static List<LoanApplication> loanApplications = new ArrayList<>();
    
    @GetMapping
    public String getLoanApplications(Model model) {
        model.addAttribute("loans", loanApplications);
        return "status";
    }
    
    @GetMapping("/apply")
    public String getLoanApplyPage(Model model) {
        model.addAttribute("loanApplication", new LoanApplication());
        return "apply";
    }
    
    @PostMapping("/submit")
    public String processLoanApplication(@Valid @ModelAttribute LoanApplication la,  BindingResult result,Model model) {
    	
    	if (result.hasErrors()) {
            return "apply";
        }
    	
    	// Determine loan status
        if (la.getCreditScore() >= 700) {
            la.setStatus(Status.APPROVED);
        } else if (la.getCreditScore() < 500) {
            la.setStatus(Status.REJECTED);
        } else {
            la.setStatus(Status.PENDING);
        }

        // Set ID and add to list
        la.setId(new Random().nextLong(10, 500000));
        loanApplications.add(la);

        return "redirect:/api/loan";
    }
    
    @GetMapping("/{id}")
    public String viewLoanApplication(@PathVariable Long id, Model model) {
        Optional<LoanApplication> loan = loanApplications.stream()
                .filter(l -> l.getId().equals(id))
                .findFirst();

        if (loan.isPresent()) {
            model.addAttribute("loan", loan.get());
        } else {
            model.addAttribute("error", "Loan Application not found with ID " + id);
        }

        return "view-application";
    }
}
