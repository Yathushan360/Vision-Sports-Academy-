package lk.sliit.visionacademy.controller;

import lk.sliit.visionacademy.dto.FinancialSummaryDTO;
import lk.sliit.visionacademy.entity.Payment;
import lk.sliit.visionacademy.service.IReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Boundary Class dedicated to Financial Reporting and Analytics.
 * Enforces Separation of Concerns by isolating aggregate executive reporting
 * from day-to-day transactional billing operations.
 */
@Controller
@RequestMapping("/reports")
public class ReportingController {

    @Autowired
    private IReportingService reportingService;

    /**
     * Web dashboard displaying financial health, revenue KPIs, and outstanding balances.
     * URL: GET /reports/financial-summary
     */
    @GetMapping("/financial-summary")
    public String viewFinancialSummary(Model model) {
        FinancialSummaryDTO summary = reportingService.getFinancialSummary();
        List<Payment> outstandingPayments = reportingService.generateOutstandingReport();

        model.addAttribute("summary", summary);
        model.addAttribute("outstandingPayments", outstandingPayments);
        model.addAttribute("revenueByType", reportingService.getRevenueByFeeType());
        model.addAttribute("statusDistribution", reportingService.getStatusDistribution());

        return "financial-report";
    }

    /**
     * Shortcut redirect from /reports to /reports/financial-summary.
     */
    @GetMapping
    public String defaultReportsRedirect() {
        return "redirect:/reports/financial-summary";
    }

    // =========================================================================
    // REST API ENDPOINTS (JSON)
    // =========================================================================

}
