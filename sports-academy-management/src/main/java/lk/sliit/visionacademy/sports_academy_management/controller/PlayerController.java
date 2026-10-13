
package lk.sliit.visionacademy.sports_academy_management.controller;

import tools.jackson.databind.JsonNode;
import lk.sliit.visionacademy.sports_academy_management.entity.AccountStatus;
import lk.sliit.visionacademy.sports_academy_management.model.Player;
import lk.sliit.visionacademy.sports_academy_management.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<Map<String, Object>> getAllPlayers(
            @RequestParam(required = false) String search) {

        List<Player> players = (search == null || search.isBlank())
                ? playerService.getAllPlayers()
                : playerService.search(search);

        return players.stream().map(this::map).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPlayerById(
            @PathVariable Long id) {

        return playerService.getPlayerById(id)
                .map(player -> ResponseEntity.ok(map(player)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Map<String, Object> createPlayer(@RequestBody JsonNode body) {
        Player player = fromJson(body);

        Player saved;

        if (hasProfileFields(body)) {
            saved = playerService.save(
                    player,
                    longValue(body, "parentId"),
                    stringValue(body, "username"),
                    stringValue(body, "password")
            );
        } else {
            saved = playerService.savePlayer(player);
        }

        return map(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePlayer(
            @PathVariable Long id,
            @RequestBody JsonNode body) {

        Player incoming = fromJson(body);

        if (playerService.getPlayerById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Player updated;

        if (hasProfileFields(body)) {
            updated = playerService.update(
                    id,
                    incoming,
                    longValue(body, "parentId")
            );
        } else {
            updated = playerService.updatePlayer(id, incoming);
        }

        if (updated == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(map(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        if (playerService.getPlayerById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/add-test-data")
    public List<Map<String, Object>> addTestPlayers() {
        List<Player> players = new ArrayList<>();

        addTestPlayer(players, "Ahamed", 16, "U16", "0771234567");
        addTestPlayer(players, "Mohamed Rifaz", 15, "U16", "0772345678");
        addTestPlayer(players, "Yashan", 14, "U14", "0773456789");
        addTestPlayer(players, "Safnas", 13, "U14", "0774567890");
        addTestPlayer(players, "Fawzan", 12, "U12", "0775678901");
        addTestPlayer(players, "Rizwan", 11, "U12", "0776789012");
        addTestPlayer(players, "Arham", 10, "U10", "0777890123");
        addTestPlayer(players, "Zayan", 9, "U10", "0778901234");
        addTestPlayer(players, "Adam", 8, "U8", "0779012345");
        addTestPlayer(players, "Imran", 7, "U8", "0770123456");

        return players.stream()
                .map(playerService::savePlayer)
                .map(this::map)
                .toList();
    }

    private void addTestPlayer(
            List<Player> players,
            String name,
            int age,
            String group,
            String contact) {

        Player player = new Player();
        player.setName(name);
        player.setAge(age);
        player.setPlayerGroup(group);
        player.setContactNumber(contact);
        players.add(player);
    }

    private Player fromJson(JsonNode body) {
        Player player = new Player();

        if (body.hasNonNull("id")) {
            player.setId(body.get("id").asLong());
        }

        player.setName(stringValue(body, "name"));

        if (body.hasNonNull("age")) {
            player.setAge(body.get("age").asInt());
        }

        player.setPlayerGroup(stringValue(body, "playerGroup"));
        player.setContactNumber(stringValue(body, "contactNumber"));

        player.setPlayerCode(stringValue(body, "playerCode"));
        player.setFullName(stringValue(body, "fullName"));
        player.setAgeGroup(stringValue(body, "ageGroup"));
        player.setMedicalInformation(stringValue(body, "medicalInformation"));
        player.setKitSize(stringValue(body, "kitSize"));

        String dob = stringValue(body, "dateOfBirth");

        if (dob != null && !dob.isBlank()) {
            player.setDateOfBirth(LocalDate.parse(dob));
        }

        String status = stringValue(body, "status");

        if (status != null && !status.isBlank()) {
            player.setStatus(AccountStatus.valueOf(status));
        }

        return player;
    }

    private boolean hasProfileFields(JsonNode body) {
        return body.has("playerCode")
                || body.has("fullName")
                || body.has("dateOfBirth")
                || body.has("ageGroup")
                || body.has("medicalInformation")
                || body.has("kitSize")
                || body.has("parentId")
                || body.has("username")
                || body.has("password")
                || body.has("status");
    }

    private String stringValue(JsonNode body, String field) {
        JsonNode value = body.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private Long longValue(JsonNode body, String field) {
        JsonNode value = body.get(field);

        return value == null || value.isNull() || value.asText().isBlank()
                ? null
                : value.asLong();
    }

    private Map<String, Object> map(Player player) {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("id", player.getId());

        // Member 2 fields
        result.put("name", player.getName());
        result.put("age", player.getAge());
        result.put("playerGroup", player.getPlayerGroup());
        result.put("contactNumber", player.getContactNumber());

        // Member 1 profile fields
        result.put("playerCode", player.getPlayerCode());
        result.put("fullName", player.getFullName());
        result.put("dateOfBirth", player.getDateOfBirth());
        result.put("ageGroup", player.getAgeGroup());
        result.put("medicalInformation", player.getMedicalInformation());
        result.put("kitSize", player.getKitSize());
        result.put("status", player.getStatus());
        result.put(
                "parentId",
                player.getParent() == null ? null : player.getParent().getId()
        );
        result.put(
                "parentName",
                player.getParent() == null ? "" : player.getParent().getFullName()
        );

        return result;
    }
}
