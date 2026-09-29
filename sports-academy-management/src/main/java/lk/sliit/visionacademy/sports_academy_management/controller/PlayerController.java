package lk.sliit.visionacademy.sports_academy_management.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import lk.sliit.visionacademy.sports_academy_management.model.Player;
import lk.sliit.visionacademy.sports_academy_management.service.PlayerService;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    // =========================================
    // GET ALL PLAYERS
    // =========================================

    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    // =========================================
    // GET PLAYER BY ID
    // =========================================

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(
            @PathVariable Long id) {

        return playerService.getPlayerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================================
    // CREATE PLAYER
    // =========================================

    @PostMapping
    public Player createPlayer(@RequestBody Player player) {
        return playerService.savePlayer(player);
    }

    // =========================================
    // UPDATE PLAYER
    // =========================================

    @PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(
            @PathVariable Long id,
            @RequestBody Player player) {

        Player updatedPlayer =
                playerService.updatePlayer(id, player);

        if (updatedPlayer != null) {
            return ResponseEntity.ok(updatedPlayer);
        }

        return ResponseEntity.notFound().build();
    }

    // =========================================
    // DELETE PLAYER
    // =========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(
            @PathVariable Long id) {

        if (playerService.getPlayerById(id).isPresent()) {

            playerService.deletePlayer(id);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // =========================================
    // ADD TEST PLAYERS
    // =========================================
    // This adds multiple players at once.
    // Useful for testing Attendance and Reports.
    // =========================================

    @PostMapping("/add-test-data")
    public List<Player> addTestPlayers() {

        List<Player> players = new ArrayList<>();

        Player player1 = new Player();
        player1.setName("Ahamed");
        player1.setAge(16);
        player1.setPlayerGroup("U16");
        player1.setContactNumber("0771234567");
        players.add(player1);

        Player player2 = new Player();
        player2.setName("Mohamed Rifaz");
        player2.setAge(15);
        player2.setPlayerGroup("U16");
        player2.setContactNumber("0772345678");
        players.add(player2);

        Player player3 = new Player();
        player3.setName("Yashan");
        player3.setAge(14);
        player3.setPlayerGroup("U14");
        player3.setContactNumber("0773456789");
        players.add(player3);

        Player player4 = new Player();
        player4.setName("Safnas");
        player4.setAge(13);
        player4.setPlayerGroup("U14");
        player4.setContactNumber("0774567890");
        players.add(player4);

        Player player5 = new Player();
        player5.setName("Fawzan");
        player5.setAge(12);
        player5.setPlayerGroup("U12");
        player5.setContactNumber("0775678901");
        players.add(player5);

        Player player6 = new Player();
        player6.setName("Rizwan");
        player6.setAge(11);
        player6.setPlayerGroup("U12");
        player6.setContactNumber("0776789012");
        players.add(player6);

        Player player7 = new Player();
        player7.setName("Arham");
        player7.setAge(10);
        player7.setPlayerGroup("U10");
        player7.setContactNumber("0777890123");
        players.add(player7);

        Player player8 = new Player();
        player8.setName("Zayan");
        player8.setAge(9);
        player8.setPlayerGroup("U10");
        player8.setContactNumber("0778901234");
        players.add(player8);

        Player player9 = new Player();
        player9.setName("Adam");
        player9.setAge(8);
        player9.setPlayerGroup("U8");
        player9.setContactNumber("0779012345");
        players.add(player9);

        Player player10 = new Player();
        player10.setName("Imran");
        player10.setAge(7);
        player10.setPlayerGroup("U8");
        player10.setContactNumber("0770123456");
        players.add(player10);

        List<Player> savedPlayers = new ArrayList<>();

        for (Player player : players) {
            savedPlayers.add(playerService.savePlayer(player));
        }

        return savedPlayers;
    }
}