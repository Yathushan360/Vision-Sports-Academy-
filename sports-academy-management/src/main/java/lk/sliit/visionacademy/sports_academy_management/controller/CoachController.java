package lk.sliit.visionacademy.sports_academy_management.controller;
import lk.sliit.visionacademy.sports_academy_management.dto.CoachRequest;
import lk.sliit.visionacademy.sports_academy_management.entity.*;
import lk.sliit.visionacademy.sports_academy_management.service.CoachService;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/coaches")
public class CoachController {
 private final CoachService service; public CoachController(CoachService service){this.service=service;}
 @GetMapping public List<Map<String,Object>> all(){return service.all().stream().map(this::map).toList();}
 @PostMapping public Map<String,Object> create(@RequestBody CoachRequest r){Coach c=new Coach();c.setCoachCode(r.coachCode());c.setFullName(r.fullName());c.setPhone(r.phone());c.setEmail(r.email());c.setCoachingLicense(r.coachingLicense());c.setAssignedGroup(r.assignedGroup());c.setStatus(r.status()==null?AccountStatus.ACTIVE:r.status());return map(service.save(c,r.username(),r.password()));}
 private Map<String,Object> map(Coach c){Map<String,Object> m=new LinkedHashMap<>();m.put("id",c.getId());m.put("coachCode",c.getCoachCode());m.put("fullName",c.getFullName());m.put("phone",c.getPhone());m.put("email",c.getEmail());m.put("coachingLicense",c.getCoachingLicense());m.put("assignedGroup",c.getAssignedGroup());m.put("status",c.getStatus());return m;}
}
