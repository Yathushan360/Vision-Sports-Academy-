package lk.sliit.visionacademy.sports_academy_management.config;
import lk.sliit.visionacademy.sports_academy_management.entity.*;
import lk.sliit.visionacademy.sports_academy_management.repository.UserAccountRepository;
import lk.sliit.visionacademy.sports_academy_management.service.PasswordService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class DataInitializer {
 @Bean CommandLineRunner seed(UserAccountRepository repo,PasswordService passwords){return args->{
  if(repo.findByUsername("admin").isEmpty()){UserAccount a=new UserAccount();a.setUsername("admin");a.setPasswordHash(passwords.encode("admin123"));a.setRole(Role.ADMIN);a.setStatus(AccountStatus.ACTIVE);repo.save(a);}
 };}
}
