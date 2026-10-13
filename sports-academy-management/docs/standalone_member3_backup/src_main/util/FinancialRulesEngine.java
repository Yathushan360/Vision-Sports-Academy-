package lk.sliit.visionacademy.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class-level Financial Rules Engine.
 * Demonstrates Core Concepts from Lecture 05/10:
 * 1. Static Variables: Shared once in class memory across all application objects (Slides 20, 23).
 * 2. Static Methods: Called directly on the class without instantiating an object (Slides 28-30).
 * 3. Static Initialization Block: Executes automatically when the class is loaded by the JVM (Slides 31-33).
 */
public final class FinancialRulesEngine {

    // STATIC VARIABLES (Class Members)
    public static final String ACADEMY_CURRENCY = "LKR";
    public static final int DEFAULT_GRACE_PERIOD_DAYS;
    public static final BigDecimal STANDARD_LATE_FEE_PENALTY;
    public static final BigDecimal REGISTRATION_SURCHARGE;
    private static final Map<String, BigDecimal> REGULATORY_RATES;

    // STATIC INITIALIZATION BLOCK (Lecture 05/10 Slides 31-33)
    // Runs only once when the class is first loaded into memory by the JVM,
    // before any objects are created or any static methods are called.
    static {
        System.out.println("[STATIC BLOCK] Initializing FinancialRulesEngine in JVM static memory...");

        DEFAULT_GRACE_PERIOD_DAYS = 7;
        STANDARD_LATE_FEE_PENALTY = new BigDecimal("500.00");
        REGISTRATION_SURCHARGE = new BigDecimal("250.00");

        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("STANDARD_TAX_RATE", BigDecimal.ZERO);
        rates.put("ACADEMY_DISCOUNT_PERCENT", new BigDecimal("5.00"));
        REGULATORY_RATES = Collections.unmodifiableMap(rates);

        System.out.println("[STATIC BLOCK] Financial rules and static penalty rates loaded successfully.");
    }

    /**
     * Private constructor to prevent instantiation, enforcing utility class semantics.
     */
    private FinancialRulesEngine() {
        throw new UnsupportedOperationException("FinancialRulesEngine is a static utility class and cannot be instantiated.");
    }

    /**
     * Static Method: Computes standard invoice due date based on shared grace period.
     * Can be called directly as FinancialRulesEngine.calculateDefaultDueDate(date).
     */
    public static LocalDate calculateDefaultDueDate(LocalDate issueDate) {
        LocalDate start = (issueDate != null) ? issueDate : LocalDate.now();
        return start.plusDays(DEFAULT_GRACE_PERIOD_DAYS);
    }

    /**
     * Static Method: Applies standard late fee penalty to an overdue balance.
     */
    public static BigDecimal applyLateFee(BigDecimal currentBalance) {
        if (currentBalance == null) {
            return STANDARD_LATE_FEE_PENALTY;
        }
        return currentBalance.add(STANDARD_LATE_FEE_PENALTY);
    }

    /**
     * Static Method: Evaluates whether a due date has lapsed relative to target assessment date.
     */
    public static boolean isOverdue(LocalDate dueDate, LocalDate assessmentDate) {
        if (dueDate == null || assessmentDate == null) {
            return false;
        }
        return dueDate.isBefore(assessmentDate);
    }

    /**
     * Static Method: Formats currency amounts with standard two-decimal precision.
     */
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "Rs. 0.00";
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return "Rs. " + df.format(amount);
    }

    /**
     * Static Method: Returns an unmodifiable view of regulatory rates.
     */
    public static Map<String, BigDecimal> getRegulatoryRates() {
        return REGULATORY_RATES;
    }
}
