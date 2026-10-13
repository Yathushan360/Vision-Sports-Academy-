package lk.sliit.visionacademy.sports_academy_management.controller;
import lk.sliit.visionacademy.sports_academy_management.dto.PlayerRequest;
import lk.sliit.visionacademy.sports_academy_management.entity.*;
import lk.sliit.visionacademy.sports_academy_management.service.PlayerService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/players")
public class PlayerController {
 private final PlayerService service; public PlayerController(PlayerService service){this.service=service;}
 @GetMapping public List<Map<String,Object>> all(@RequestParam(required=false) String search){return service.search(search).stream().map(this::map).toList();}
 @GetMapping("/{id}") public Map<String,Object> get(@PathVariable Long id){return map(service.get(id));}
 @PostMapping public Map<String,Object> create(@RequestBody PlayerRequest r){Player p=new Player();copy(p,r);return map(service.save(p,r.parentId(),r.username(),r.password()));}
 @PutMapping("/{id}") public Map<String,Object> update(@PathVariable Long id,@RequestBody PlayerRequest r){Player p=new Player();copy(p,r);return map(service.update(id,p,r.parentId()));}
 @DeleteMapping("/{id}") public Map<String,Object> delete(@PathVariable Long id){service.delete(id);return Map.of("success",true);}
 private void copy(Player p,PlayerRequest r){p.setPlayerCode(r.playerCode());p.setFullName(r.fullName());p.setDateOfBirth(r.dateOfBirth());p.setAgeGroup(r.ageGroup());p.setMedicalInformation(r.medicalInformation());p.setKitSize(r.kitSize());p.setStatus(r.status()==null?AccountStatus.ACTIVE:r.status());}
 private Map<String,Object> map(Player p){Map<String,Object> m=new LinkedHashMap<>();m.put("id",p.getId());m.put("playerCode",p.getPlayerCode());m.put("fullName",p.getFullName());m.put("dateOfBirth",p.getDateOfBirth());m.put("ageGroup",p.getAgeGroup());m.put("medicalInformation",p.getMedicalInformation());m.put("kitSize",p.getKitSize());m.put("status",p.getStatus());m.put("parentId",p.getParent()==null?null:p.getParent().getId());m.put("parentName",p.getParent()==null?"":p.getParent().getFullName());return m;}
}
