package lk.sliit.visionacademy.sports_academy_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SportsAcademyManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(SportsAcademyManagementApplication.class, args);
	}

}

