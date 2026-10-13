
package lk.sliit.visionacademy.sports_academy_management.repository;

import lk.sliit.visionacademy.sports_academy_management.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByFullNameContainingIgnoreCaseOrPlayerCodeContainingIgnoreCase(
            String fullName,
            String playerCode
    );

    List<Player> findByAgeGroupIgnoreCase(String ageGroup);

    long countByParent_Id(Long parentId);
}
