
package lk.sliit.visionacademy.sports_academy_management.service;

import lk.sliit.visionacademy.sports_academy_management.entity.AccountStatus;
import lk.sliit.visionacademy.sports_academy_management.entity.ParentGuardian;
import lk.sliit.visionacademy.sports_academy_management.entity.Role;
import lk.sliit.visionacademy.sports_academy_management.entity.UserAccount;
import lk.sliit.visionacademy.sports_academy_management.model.Player;
import lk.sliit.visionacademy.sports_academy_management.repository.ParentGuardianRepository;
import lk.sliit.visionacademy.sports_academy_management.repository.PlayerRepository;
import lk.sliit.visionacademy.sports_academy_management.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final ParentGuardianRepository parents;
    private final UserAccountRepository users;
    private final PasswordService passwords;

    public PlayerService(
            PlayerRepository playerRepository,
            ParentGuardianRepository parents,
            UserAccountRepository users,
            PasswordService passwords) {

        this.playerRepository = playerRepository;
        this.parents = parents;
        this.users = users;
        this.passwords = passwords;
    }

    // Member 2: existing player CRUD methods

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    public Player savePlayer(Player player) {
        synchronizeLegacyFields(player);

        if (player.getStatus() == null) {
            player.setStatus(AccountStatus.ACTIVE);
        }

        return playerRepository.save(player);
    }

    public Player updatePlayer(Long id, Player incoming) {
        Optional<Player> result = playerRepository.findById(id);

        if (result.isEmpty()) {
            return null;
        }

        Player current = result.get();

        current.setName(incoming.getName());
        current.setAge(incoming.getAge());
        current.setPlayerGroup(incoming.getPlayerGroup());
        current.setContactNumber(incoming.getContactNumber());

        if (incoming.getPlayerCode() != null) {
            current.setPlayerCode(incoming.getPlayerCode());
        }

        if (incoming.getFullName() != null) {
            current.setFullName(incoming.getFullName());
        }

        current.setDateOfBirth(incoming.getDateOfBirth());

        if (incoming.getAgeGroup() != null) {
            current.setAgeGroup(incoming.getAgeGroup());
        }

        current.setMedicalInformation(incoming.getMedicalInformation());
        current.setKitSize(incoming.getKitSize());

        if (incoming.getStatus() != null) {
            current.setStatus(incoming.getStatus());
        }

        synchronizeLegacyFields(current);
        synchronizeUserStatus(current);

        return playerRepository.save(current);
    }

    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }

    // Member 1: player listing and search

    public List<Player> all() {
        return getAllPlayers();
    }

    public Player get(Long id) {
        return playerRepository.findById(id).orElseThrow();
    }

    public List<Player> search(String query) {
        if (query == null || query.isBlank()) {
            return all();
        }

        String term = query.trim();

        List<Player> results = new ArrayList<>(
                playerRepository.findByAgeGroupIgnoreCase(term)
        );

        for (Player player :
                playerRepository.findByFullNameContainingIgnoreCaseOrPlayerCodeContainingIgnoreCase(
                        term, term)) {

            if (!results.contains(player)) {
                results.add(player);
            }
        }

        return results;
    }

    // Member 1: profile creation, parent linking and optional login

    @Transactional
    public Player save(
            Player player,
            Long parentId,
            String username,
            String password) {

        if (player.getPlayerCode() == null
                || player.getPlayerCode().isBlank()) {

            player.setPlayerCode(
                    "PLY-" + System.currentTimeMillis() % 100000
            );
        }

        linkParent(player, parentId);

        if (player.getStatus() == null) {
            player.setStatus(AccountStatus.ACTIVE);
        }

        synchronizeLegacyFields(player);

        if (player.getUserAccount() == null
                && username != null
                && !username.isBlank()) {

            UserAccount account = new UserAccount();
            account.setUsername(username);
            account.setPasswordHash(
                    passwords.encode(
                            password == null || password.isBlank()
                                    ? "player123"
                                    : password
                    )
            );
            account.setRole(Role.PLAYER);
            account.setStatus(player.getStatus());

            player.setUserAccount(users.save(account));
        }

        return playerRepository.save(player);
    }

    // Member 1: profile update, parent link and login status sync

    @Transactional
    public Player update(
            Long id,
            Player incoming,
            Long parentId) {

        Player current = get(id);

        if (incoming.getPlayerCode() != null) {
            current.setPlayerCode(incoming.getPlayerCode());
        }

        current.setFullName(incoming.getFullName());
        current.setDateOfBirth(incoming.getDateOfBirth());
        current.setAgeGroup(incoming.getAgeGroup());
        current.setMedicalInformation(incoming.getMedicalInformation());
        current.setKitSize(incoming.getKitSize());

        AccountStatus status = incoming.getStatus() == null
                ? AccountStatus.ACTIVE
                : incoming.getStatus();

        current.setStatus(status);

        linkParent(current, parentId);
        synchronizeLegacyFields(current);
        synchronizeUserStatus(current);

        return playerRepository.save(current);
    }

    @Transactional
    public void delete(Long id) {
        playerRepository.deleteById(id);
    }

    private void linkParent(Player player, Long parentId) {
        if (parentId == null) {
            player.setParent(null);
        } else {
            ParentGuardian parent = parents.findById(parentId)
                    .orElseThrow(() ->
                            new RuntimeException("Parent not found: " + parentId));

            player.setParent(parent);
        }
    }

    private void synchronizeUserStatus(Player player) {
        if (player.getUserAccount() != null
                && player.getStatus() != null) {

            player.getUserAccount().setStatus(player.getStatus());
            users.save(player.getUserAccount());
        }
    }

    private void synchronizeLegacyFields(Player player) {
        if (player.getFullName() == null
                && player.getName() != null) {

            player.setFullName(player.getName());
        }

        if (player.getName() == null
                && player.getFullName() != null) {

            player.setName(player.getFullName());
        }

        if (player.getAgeGroup() == null
                && player.getPlayerGroup() != null) {

            player.setAgeGroup(player.getPlayerGroup());
        }

        if (player.getPlayerGroup() == null
                && player.getAgeGroup() != null) {

            player.setPlayerGroup(player.getAgeGroup());
        }

        LocalDate dob = player.getDateOfBirth();

        if (dob != null) {
            player.setAge(
                    Math.max(0, Period.between(dob, LocalDate.now()).getYears())
            );
        }
    }
}
