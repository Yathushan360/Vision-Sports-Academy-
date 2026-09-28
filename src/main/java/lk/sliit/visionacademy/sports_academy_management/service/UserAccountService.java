package lk.sliit.visionacademy.sports_academy_management.service;

import lk.sliit.visionacademy.sports_academy_management.entity.*;
import lk.sliit.visionacademy.sports_academy_management.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserAccountService {
    private final UserAccountRepository repo;
    private final PasswordService passwords;
    public UserAccountService(UserAccountRepository repo, PasswordService passwords){this.repo=repo;this.passwords=passwords;}
    public Optional<UserAccount> authenticate(String username,String password){
        return repo.findByUsername(username).filter(u -> u.getStatus()==AccountStatus.ACTIVE && passwords.matches(password,u.getPasswordHash()));
    }
    public UserAccount create(String username,String password,Role role){
        UserAccount u=new UserAccount(); u.setUsername(username); u.setPasswordHash(passwords.encode(password)); u.setRole(role); u.setStatus(AccountStatus.ACTIVE); return repo.save(u);
    }
    public UserAccount findByUsername(String username){ return repo.findByUsername(username).orElse(null); }
}
