package lk.sliit.visionacademy.sports_academy_management.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class PageController {
 @GetMapping({"/","/login"}) public String login(){return "login";}
 @GetMapping("/dashboard") public String dashboard(){return "dashboard";}
 @GetMapping("/players") public String players(){return "players";}
 @GetMapping("/parents") public String parents(){return "parents";}
 @GetMapping("/coaches") public String coaches(){return "coaches";}
}
