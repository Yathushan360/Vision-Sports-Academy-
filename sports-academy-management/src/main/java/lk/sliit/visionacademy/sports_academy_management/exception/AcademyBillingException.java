package lk.sliit.visionacademy.sports_academy_management.exception;

/**
 * Base Abstract Checked Exception for all Billing & Payments domain operations.
 * Demonstrates Core Concepts from Lecture 10/11:
 * 1. Exception Hierarchy: Subclass of Exception (Throwable -> Exception -> AcademyBillingException) (Slides 16, 21).
 * 2. Checked Exception: Subject to the Java "Catch or Specify Requirement" (Slide 18, 41).
 * 3. Custom Exception Subclass: Chaining to super(message) (Slide 47-49).
 */
public abstract class AcademyBillingException extends Exception {

    public AcademyBillingException(String message) {
        super(message);
    }

    public AcademyBillingException(String message, Throwable cause) {
        super(message, cause);
    }
}

