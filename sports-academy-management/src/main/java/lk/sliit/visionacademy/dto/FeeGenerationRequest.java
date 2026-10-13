package lk.sliit.visionacademy.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Data Transfer Object for generating and assigning a fee to a player.
 * Validates mandatory identifiers and ensures clean encapsulation of fee generation inputs.
 */
public class FeeGenerationRequest {

    @NotNull(message = "Player ID is required")
    @Min(value = 1, message = "Player ID must be a positive number")
    private Integer playerId;

    @NotNull(message = "Fee Structure ID is required")
    @Min(value = 1, message = "Fee Structure ID must be a positive number")
    private Integer feeId;

    private LocalDate dueDate;

    public FeeGenerationRequest() {
    }

    public FeeGenerationRequest(Integer playerId, Integer feeId, LocalDate dueDate) {
        this.playerId = playerId;
        this.feeId = feeId;
        this.dueDate = dueDate;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public Integer getFeeId() {
        return feeId;
    }

    public void setFeeId(Integer feeId) {
        this.feeId = feeId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
