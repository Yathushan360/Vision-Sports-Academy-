package lk.sliit.visionacademy.sports_academy_management.repository;

import lk.sliit.visionacademy.sports_academy_management.entity.FeeStructure;
import lk.sliit.visionacademy.sports_academy_management.entity.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Integer> {

    /**
     * Finds fee configurations filtered by fee type (e.g. MONTHLY_TRAINING, REGISTRATION).
     */
    List<FeeStructure> findByFeeType(FeeType feeType);

    /**
     * Finds fee configurations filtered by student age group.
     */
    List<FeeStructure> findByAgeGroup(String ageGroup);
}

