package lk.sliit.visionacademy.sports_academy_management.service;

import lk.sliit.visionacademy.sports_academy_management.entity.FeeStructure;
import lk.sliit.visionacademy.sports_academy_management.entity.FeeType;
import lk.sliit.visionacademy.sports_academy_management.exception.PaymentProcessingException;
import lk.sliit.visionacademy.sports_academy_management.repository.FeeStructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service implementation for managing Academy Fee Structures.
 * Demonstrates High Cohesion (purely handles fee tier configuration) and Domain-Driven Design.
 */
@Service
public class FeeStructureService implements IFeeStructureService {

    @Autowired
    private FeeStructureRepository feeStructureRepository;

    @Override
    public List<FeeStructure> getAllFeeStructures() {
        return feeStructureRepository.findAll();
    }

    @Override
    public FeeStructure getFeeStructureById(Integer feeId) throws PaymentProcessingException {
        return feeStructureRepository.findById(feeId)
                .orElseThrow(() -> new PaymentProcessingException("Fee tier not found with ID: " + feeId));
    }

    @Override
    public FeeStructure createFeeStructure(FeeStructure feeStructure) {
        if (feeStructure.getEffectiveFrom() == null) {
            feeStructure.setEffectiveFrom(LocalDate.now());
        }
        return feeStructureRepository.save(feeStructure);
    }

    @Override
    public FeeStructure updateFeeStructure(Integer feeId, FeeStructure updatedFee) throws PaymentProcessingException {
        FeeStructure existing = getFeeStructureById(feeId);
        existing.setFeeType(updatedFee.getFeeType());
        existing.setAmount(updatedFee.getAmount());
        existing.setAgeGroup(updatedFee.getAgeGroup());
        if (updatedFee.getEffectiveFrom() != null) {
            existing.setEffectiveFrom(updatedFee.getEffectiveFrom());
        }
        return feeStructureRepository.save(existing);
    }

    @Override
    public void deleteFeeStructure(Integer feeId) throws PaymentProcessingException {
        FeeStructure existing = getFeeStructureById(feeId);
        feeStructureRepository.delete(existing);
    }

    @Override
    public List<FeeStructure> getFeeStructuresByType(FeeType feeType) {
        return feeStructureRepository.findByFeeType(feeType);
    }
}

