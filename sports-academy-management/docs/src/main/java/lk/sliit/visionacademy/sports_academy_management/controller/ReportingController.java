package lk.sliit.visionacademy.sports_academy_management.controller;

import lk.sliit.visionacademy.sports_academy_management.dto.FinancialSummaryDTO;
import lk.sliit.visionacademy.sports_academy_management.entity.Payment;
import lk.sliit.visionacademy.sports_academy_management.service.IReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
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
        model.addAttribute("agingSchedule", reportingService.getAgingSchedule());
        model.addAttribute("sortedDueDates", reportingService.getDistinctDueDatesSorted());

        return "financial-report";
    }

    /**
     * Downloadable CSV export of all outstanding (PENDING and OVERDUE) payments.
     * URL: GET /reports/export/csv
     * Fulfills Use Case: Generate & Download Reports.
     */
    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportOutstandingCsv() {
        byte[] csvData = reportingService.generateOutstandingReportCsv();
        String filename = "vision_academy_outstanding_report_" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .body(csvData);
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

    @GetMapping("/api/summary")
    @ResponseBody
    public ResponseEntity<FinancialSummaryDTO> getSummaryApi() {
        return ResponseEntity.ok(reportingService.getFinancialSummary());
    }

    @GetMapping("/api/outstanding")
    @ResponseBody
    public ResponseEntity<List<Payment>> getOutstandingReportApi() {
        return ResponseEntity.ok(reportingService.generateOutstandingReport());
    }
}

