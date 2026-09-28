package lk.sliit.visionacademy.sports_academy_management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_accounts")
public class UserAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true, length=50)
    private String username;
    @Column(name="password_hash", nullable=false)
    private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20)
    private Role role;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20)
    private AccountStatus status = AccountStatus.ACTIVE;
    @Column(name="created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getUsername(){return username;} public void setUsername(String username){this.username=username;}
    public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String passwordHash){this.passwordHash=passwordHash;}
    public Role getRole(){return role;} public void setRole(Role role){this.role=role;}
    public AccountStatus getStatus(){return status;} public void setStatus(AccountStatus status){this.status=status;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime createdAt){this.createdAt=createdAt;}
}
