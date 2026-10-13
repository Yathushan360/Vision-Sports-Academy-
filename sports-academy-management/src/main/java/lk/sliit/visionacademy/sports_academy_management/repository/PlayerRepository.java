package lk.sliit.visionacademy.sports_academy_management.repository;

import lk.sliit.visionacademy.sports_academy_management.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

 List<Player> findByFullNameContainingIgnoreCaseOrPlayerCodeContainingIgnoreCase(
         String name,
         String code
 );

 List<Player> findByAgeGroupIgnoreCase(String ageGroup);

 long countByParentId(Long parentId);
}