package lk.sliit.visionacademy.sports_academy_management.service;

import lk.sliit.visionacademy.sports_academy_management.entity.*;
import lk.sliit.visionacademy.sports_academy_management.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository players;
    private final ParentGuardianRepository parents;
    private final UserAccountRepository users;
    private final PasswordService passwords;

    public PlayerService(
            PlayerRepository players,
            ParentGuardianRepository parents,
            UserAccountRepository users,
            PasswordService passwords) {

        this.players = players;
        this.parents = parents;
        this.users = users;
        this.passwords = passwords;
    }

    public List<Player> all() {
        return players.findAll();
    }

    public Player get(Long id) {
        return players.findById(id).orElseThrow();
    }

    public List<Player> search(String q) {

        if (q == null || q.isBlank()) {
            return all();
        }

        String s = q.trim();

        List<Player> byAge =
                players.findByAgeGroupIgnoreCase(s);

        List<Player> byNameCode =
                players.findByFullNameContainingIgnoreCaseOrPlayerCodeContainingIgnoreCase(s, s);

        byAge.addAll(
                byNameCode.stream()
                        .filter(p -> !byAge.contains(p))
                        .toList()
        );

        return byAge;
    }

    public Player save(
            Player p,
            Long parentId,
            String username,
            String password) {

        if (p.getPlayerCode() == null || p.getPlayerCode().isBlank()) {
            p.setPlayerCode(
                    "PLY-" + System.currentTimeMillis() % 100000
            );
        }

        if (parentId != null) {
            p.setParent(
                    parents.findById(parentId).orElseThrow()
            );
        } else {
            p.setParent(null);
        }

        if (p.getStatus() == null) {
            p.setStatus(AccountStatus.ACTIVE);
        }

        /*
         * Create the optional login account.
         * The login account status now matches the player status.
         */
        if (p.getUserAccount() == null
                && username != null
                && !username.isBlank()) {

            UserAccount u = new UserAccount();

            u.setUsername(username);

            u.setPasswordHash(
                    passwords.encode(
                            password == null || password.isBlank()
                                    ? "player123"
                                    : password
                    )
            );

            u.setRole(Role.PLAYER);

            // Keep login account status synchronized
            // with the player's account status.
            u.setStatus(p.getStatus());

            p.setUserAccount(users.save(u));
        }

        return players.save(p);
    }

    public Player update(
            Long id,
            Player incoming,
            Long parentId) {

        Player p = get(id);

        p.setFullName(incoming.getFullName());
        p.setDateOfBirth(incoming.getDateOfBirth());
        p.setAgeGroup(incoming.getAgeGroup());
        p.setMedicalInformation(incoming.getMedicalInformation());
        p.setKitSize(incoming.getKitSize());

        AccountStatus newStatus =
                incoming.getStatus() == null
                        ? AccountStatus.ACTIVE
                        : incoming.getStatus();

        // Update player status
        p.setStatus(newStatus);

        // Keep linked login account status synchronized
        if (p.getUserAccount() != null) {
            p.getUserAccount().setStatus(newStatus);
            users.save(p.getUserAccount());
        }

        // Update parent/guardian link
        if (parentId == null) {
            p.setParent(null);
        } else {
            p.setParent(
                    parents.findById(parentId).orElseThrow()
            );
        }

        return players.save(p);
    }

    public void delete(Long id) {
        players.deleteById(id);
    }
}