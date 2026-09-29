package lk.sliit.visionacademy.repository;

import lk.sliit.visionacademy.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Integer> {
}
