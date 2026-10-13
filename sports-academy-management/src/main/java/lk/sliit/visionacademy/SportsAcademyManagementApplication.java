package lk.sliit.visionacademy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "lk.sliit.visionacademy.sports_academy_management")
@EntityScan(basePackages = {
        "lk.sliit.visionacademy.sports_academy_management.entity",
        "lk.sliit.visionacademy.sports_academy_management.model"
})
@EnableJpaRepositories(basePackages = "lk.sliit.visionacademy.sports_academy_management.repository")
@EnableScheduling
public class SportsAcademyManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(SportsAcademyManagementApplication.class, args);
    }
}
