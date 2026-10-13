package lk.sliit.visionacademy.sports_academy_management.controller;

import lk.sliit.visionacademy.sports_academy_management.dto.ParentRequest;
import lk.sliit.visionacademy.sports_academy_management.entity.*;
import lk.sliit.visionacademy.sports_academy_management.service.ParentService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/parents")
public class ParentController {

 private final ParentService service;

 public ParentController(ParentService service) {
  this.service = service;
 }

 @GetMapping
 public List<Map<String, Object>> all() {

  return service.all()
          .stream()
          .map(this::map)
          .toList();
 }

 @PostMapping
 public Map<String, Object> create(
         @RequestBody ParentRequest r) {

  ParentGuardian p = new ParentGuardian();

  p.setParentCode(r.parentCode());
  p.setFullName(r.fullName());
  p.setPhone(r.phone());
  p.setEmail(r.email());
  p.setAddress(r.address());

  p.setStatus(
          r.status() == null
                  ? AccountStatus.ACTIVE
                  : r.status()
  );

  ParentGuardian saved =
          service.save(
                  p,
                  r.username(),
                  r.password()
          );

  return map(saved);
 }

 @PutMapping("/{parentId}/players")
 public Map<String, Object> linkPlayers(
         @PathVariable Long parentId,
         @RequestBody Map<String, Object> body) {

  List<?> rawIds =
          (List<?>) body.getOrDefault(
                  "playerIds",
                  List.of()
          );

  List<Long> playerIds =
          rawIds.stream()
                  .map(id -> Long.valueOf(id.toString()))
                  .toList();

  ParentGuardian parent =
          service.linkPlayers(
                  parentId,
                  playerIds
          );

  return map(parent);
 }

 private Map<String, Object> map(
         ParentGuardian p) {

  Map<String, Object> m =
          new LinkedHashMap<>();

  m.put("id", p.getId());
  m.put("parentCode", p.getParentCode());
  m.put("fullName", p.getFullName());
  m.put("phone", p.getPhone());
  m.put("email", p.getEmail());
  m.put("address", p.getAddress());
  m.put("status", p.getStatus());

  m.put(
          "playerCount",
          service.playerCount(p.getId())
  );

  return m;
 }
}