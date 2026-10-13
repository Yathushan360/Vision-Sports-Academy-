package lk.sliit.visionacademy.service;

import lk.sliit.visionacademy.entity.Payment;
import lk.sliit.visionacademy.util.FinancialRulesEngine;
import org.springframework.stereotype.Component;

/**
 * Service/Utility component dedicated to formatting and generating printable payment receipts.
 *
 * OOAD CONCEPT: DEPENDENCY RELATIONSHIP ("Uses-A") (Lecture 04 Slides 40-52, Lecture 06.1):
 * - Represented by a dashed open arrow (- - - >) in UML.
 * - PaymentReceiptPrinter depends on Payment, but DOES NOT maintain a persistent reference
 *   or attribute storing a Payment instance (no instance field of type Payment).
 * - Instead, Payment is passed temporarily as a method parameter (printReceipt(Payment payment)).
 * - The lifetime of Payment is completely independent of PaymentReceiptPrinter.
 * - If the Payment class structure changes (e.g. methods/getters), PaymentReceiptPrinter may
 *   need modifications, which is the textbook definition of an OOAD Dependency.
 */
@Component
public class PaymentReceiptPrinter {

    /**
     * Formats receipt output using a temporary method parameter dependency on Payment.
     *
     * @param payment The payment entity passed as a method argument (Dependency)
     * @return Formatted printable receipt string
     */
    public String printReceipt(Payment payment) {
        if (payment == null) {
            return "RECEIPT GENERATION ERROR: Payment instance cannot be null.";
        }

        String feeCategory = (payment.getFeeStructure() != null && payment.getFeeStructure().getFeeType() != null)
                ? payment.getFeeStructure().getFeeType().name()
                : "GENERAL_TRAINING";

        String ageGroup = (payment.getFeeStructure() != null && payment.getFeeStructure().getAgeGroup() != null)
                ? payment.getFeeStructure().getAgeGroup()
                : "All Ages";

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("             VISION SPORTS ACADEMY                  \n");
        sb.append("             OFFICIAL PAYMENT RECEIPT               \n");
        sb.append("====================================================\n");
        sb.append(String.format("Receipt Ref     : REC-%06d\n", payment.getPaymentId() != null ? payment.getPaymentId() : 0));
        sb.append(String.format("Student/Player  : Player #%d\n", payment.getPlayerId() != null ? payment.getPlayerId() : 0));
        sb.append(String.format("Fee Tier        : %s (%s)\n", feeCategory, ageGroup));
        sb.append(String.format("Amount          : %s\n", FinancialRulesEngine.formatCurrency(payment.getAmount())));
        sb.append(String.format("Payment Status  : %s\n", payment.getStatus() != null ? payment.getStatus().name() : "UNKNOWN"));
        sb.append(String.format("Settlement Date : %s\n", payment.getPaidDate() != null ? payment.getPaidDate() : "PENDING"));
        sb.append(String.format("Payment Method  : %s\n", payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "N/A"));
        sb.append(String.format("Transaction Ref : %s\n", payment.getTransactionReference() != null ? payment.getTransactionReference() : "NONE"));
        sb.append("----------------------------------------------------\n");
        sb.append("Status Invariant: Verification Stamp: VALIDATED\n");
        sb.append("Official Support: support@visionacademy.lk\n");
        sb.append("====================================================\n");
        sb.append("         Thank you for choosing Vision!             \n");
        sb.append("====================================================\n");

        return sb.toString();
    }
}
