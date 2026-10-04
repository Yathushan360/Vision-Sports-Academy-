package lk.sliit.visionacademy.controller;

import lk.sliit.visionacademy.entity.FeeStructure;
import lk.sliit.visionacademy.entity.FeeType;
import lk.sliit.visionacademy.exception.PaymentProcessingException;
import lk.sliit.visionacademy.service.IFeeStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * Boundary Class dedicated to Fee Structure Management.
 * Enforces High Cohesion and Single Responsibility Principle by isolating
 * fee rate configurations from transaction execution.
 */
@Controller
@RequestMapping("/fee-structures")
public class FeeStructureController {

    @Autowired
    private IFeeStructureService feeStructureService;

    // =========================================================================
    // UI ENDPOINTS (Thymeleaf Views)
    // =========================================================================

    /**
     * Web page for Admin to manage academy fee structures and rates.
     * URL: GET /fee-structures
     */
    @GetMapping
    public String viewFeeStructures(Model model) {
        model.addAttribute("feeStructures", feeStructureService.getAllFeeStructures());
        model.addAttribute("feeTypes", FeeType.values());
        
        FeeStructure newTier = new FeeStructure();
        newTier.setEffectiveFrom(LocalDate.now());
        model.addAttribute("newTier", newTier);

        return "fee-structures";
    }

    /**
     * Web form action to create a new fee tier.
     * URL: POST /fee-structures/create
     */
    @PostMapping("/create")
    public String createFeeStructure(@ModelAttribute("newTier") FeeStructure feeStructure,
                                     RedirectAttributes redirectAttributes) {
        try {
            feeStructureService.createFeeStructure(feeStructure);
            redirectAttributes.addFlashAttribute("success", "Fee tier (" + feeStructure.getFeeType() + ") created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create fee tier: " + e.getMessage());
        }
        return "redirect:/fee-structures";
    }

    /**
     * Web action to remove an existing fee tier.
     * URL: POST /fee-structures/delete/{id}
     */
    @PostMapping("/delete/{id}")
    public String deleteFeeStructure(@PathVariable("id") Integer id,
                                     RedirectAttributes redirectAttributes) {
        try {
            feeStructureService.deleteFeeStructure(id);
            redirectAttributes.addFlashAttribute("success", "Fee tier #" + id + " deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete fee tier: " + e.getMessage());
        }
        return "redirect:/fee-structures";
    }

    // =========================================================================
    // REST API ENDPOINTS (JSON)
    // =========================================================================


}
