package lk.sliit.visionacademy.service;

import lk.sliit.visionacademy.entity.FeeStructure;
import lk.sliit.visionacademy.entity.FeeType;
import lk.sliit.visionacademy.exception.PaymentProcessingException;

import java.util.List;

/**
 * Service Contract for Academy Fee Structure Configuration.
 * Enforces High Cohesion and Domain-Driven Design by managing fee tiers independently.
 */
public interface IFeeStructureService {

    List<FeeStructure> getAllFeeStructures();

    FeeStructure getFeeStructureById(Integer feeId) throws PaymentProcessingException;

    FeeStructure createFeeStructure(FeeStructure feeStructure);

    FeeStructure updateFeeStructure(Integer feeId, FeeStructure updatedFee) throws PaymentProcessingException;

    void deleteFeeStructure(Integer feeId) throws PaymentProcessingException;

    List<FeeStructure> getFeeStructuresByType(FeeType feeType);
}
