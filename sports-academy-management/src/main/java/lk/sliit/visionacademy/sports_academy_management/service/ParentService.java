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

import java.util.List;

@Service
public class ParentService {

    private final ParentGuardianRepository parents;
    private final PlayerRepository players;
    private final UserAccountRepository users;
    private final PasswordService passwords;

    public ParentService(
            ParentGuardianRepository parents,
            PlayerRepository players,
            UserAccountRepository users,
            PasswordService passwords) {

        this.parents = parents;
        this.players = players;
        this.users = users;
        this.passwords = passwords;
    }

    public List<ParentGuardian> all() {
        return parents.findAll();
    }

    public ParentGuardian save(
            ParentGuardian parent,
            String username,
            String password) {

        if (parent.getParentCode() == null
                || parent.getParentCode().isBlank()) {
            parent.setParentCode(
                    "PAR-" + System.currentTimeMillis() % 100000
            );
        }

        if (parent.getStatus() == null) {
            parent.setStatus(AccountStatus.ACTIVE);
        }

        if (parent.getUserAccount() == null
                && username != null
                && !username.isBlank()) {

            UserAccount account = new UserAccount();
            account.setUsername(username);

            account.setPasswordHash(
                    passwords.encode(
                            password == null || password.isBlank()
                                    ? "parent123"
                                    : password
                    )
            );

            account.setRole(Role.PARENT);
            account.setStatus(parent.getStatus());

            parent.setUserAccount(users.save(account));
        }

        return parents.save(parent);
    }

    public ParentGuardian get(Long id) {
        return parents.findById(id).orElseThrow();
    }

    @Transactional
    public ParentGuardian linkPlayers(
            Long parentId,
            List<Long> playerIds) {

        ParentGuardian parent = parents.findById(parentId)
                .orElseThrow(() ->
                        new RuntimeException("Parent not found: " + parentId)
                );

        // Remove existing player links belonging to this parent.
        List<Player> allPlayers = players.findAll();

        for (Player player : allPlayers) {
            if (player.getParent() != null
                    && player.getParent().getId().equals(parentId)) {

                player.setParent(null);
                players.save(player);
            }
        }

        // Link the selected players to the parent.
        if (playerIds != null) {
            for (Long playerId : playerIds) {

                Player player = players.findById(playerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Player not found: " + playerId
                                )
                        );

                player.setParent(parent);
                players.save(player);
            }
        }

        return parent;
    }

    public long playerCount(Long parentId) {
        return players.countByParent_Id(parentId);
    }
}