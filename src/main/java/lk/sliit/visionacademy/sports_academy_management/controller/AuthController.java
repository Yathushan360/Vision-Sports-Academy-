package lk.sliit.visionacademy.sports_academy_management.controller;

import jakarta.servlet.http.HttpSession;
import lk.sliit.visionacademy.sports_academy_management.entity.UserAccount;
import lk.sliit.visionacademy.sports_academy_management.service.UserAccountService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
 private final UserAccountService users;
 public AuthController(UserAccountService users){this.users=users;}
 @PostMapping("/login") public Map<String,Object> login(@RequestBody Map<String,String> body,HttpSession session){
  String username=body.getOrDefault("username",""); String password=body.getOrDefault("password","");
  UserAccount u=users.authenticate(username,password).orElse(null);
  if(u==null) return Map.of("success",false,"message","Invalid username/password or inactive account.");
  session.setAttribute("user",u.getUsername()); session.setAttribute("role",u.getRole().name());
  return Map.of("success",true,"username",u.getUsername(),"role",u.getRole().name());
 }
 @PostMapping("/logout") public Map<String,Object> logout(HttpSession session){session.invalidate();return Map.of("success",true);}
 @GetMapping("/me") public Map<String,Object> me(HttpSession session){Object u=session.getAttribute("user");return u==null?Map.of("authenticated",false):Map.of("authenticated",true,"username",u,"role",session.getAttribute("role"));}
}
