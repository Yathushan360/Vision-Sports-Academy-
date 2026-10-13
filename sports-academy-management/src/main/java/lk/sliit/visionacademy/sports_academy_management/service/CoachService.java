package lk.sliit.visionacademy.sports_academy_management.service;
import lk.sliit.visionacademy.sports_academy_management.entity.*;
import lk.sliit.visionacademy.sports_academy_management.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class CoachService {
 private final CoachRepository coaches; private final UserAccountRepository users; private final PasswordService passwords;
 public CoachService(CoachRepository coaches,UserAccountRepository users,PasswordService passwords){this.coaches=coaches;this.users=users;this.passwords=passwords;}
 public List<Coach> all(){return coaches.findAll();}
 public Coach save(Coach c,String username,String password){
  if(c.getCoachCode()==null||c.getCoachCode().isBlank())c.setCoachCode("COA-"+System.currentTimeMillis()%100000);
  if(c.getStatus()==null)c.setStatus(AccountStatus.ACTIVE);
  if(c.getUserAccount()==null&&username!=null&&!username.isBlank()){UserAccount u=new UserAccount();u.setUsername(username);u.setPasswordHash(passwords.encode(password==null||password.isBlank()?"coach123":password));u.setRole(Role.COACH);u.setStatus(AccountStatus.ACTIVE);c.setUserAccount(users.save(u));}
  return coaches.save(c);
 }
}
