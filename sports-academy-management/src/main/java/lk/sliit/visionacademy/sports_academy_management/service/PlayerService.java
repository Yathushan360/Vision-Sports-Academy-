package lk.sliit.visionacademy.sports_academy_management.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lk.sliit.visionacademy.sports_academy_management.model.Player;
import lk.sliit.visionacademy.sports_academy_management.repository.PlayerRepository;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    public Player updatePlayer(Long id, Player player) {

        Optional<Player> existingPlayer =
                playerRepository.findById(id);

        if (existingPlayer.isPresent()) {

            Player currentPlayer = existingPlayer.get();

            currentPlayer.setName(player.getName());
            currentPlayer.setAge(player.getAge());
            currentPlayer.setPlayerGroup(player.getPlayerGroup());
            currentPlayer.setContactNumber(player.getContactNumber());

            return playerRepository.save(currentPlayer);
        }

        return null;
    }

    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }
}