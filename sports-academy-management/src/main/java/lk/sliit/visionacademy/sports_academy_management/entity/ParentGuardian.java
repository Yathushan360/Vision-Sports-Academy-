package lk.sliit.visionacademy.sports_academy_management.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="parent_guardians")
public class ParentGuardian {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="parent_code", nullable=false, unique=true) private String parentCode;
    @Column(name="full_name", nullable=false) private String fullName;
    @Column(nullable=false) private String phone;
    private String email;
    private String address;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private AccountStatus status=AccountStatus.ACTIVE;
    @OneToOne @JoinColumn(name="user_account_id", unique=true) private UserAccount userAccount;
    @OneToMany(mappedBy="parent") private List<Player> players = new ArrayList<>();

    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getParentCode(){return parentCode;} public void setParentCode(String v){this.parentCode=v;}
    public String getFullName(){return fullName;} public void setFullName(String v){this.fullName=v;}
    public String getPhone(){return phone;} public void setPhone(String v){this.phone=v;}
    public String getEmail(){return email;} public void setEmail(String v){this.email=v;}
    public String getAddress(){return address;} public void setAddress(String v){this.address=v;}
    public AccountStatus getStatus(){return status;} public void setStatus(AccountStatus v){this.status=v;}
    public UserAccount getUserAccount(){return userAccount;} public void setUserAccount(UserAccount v){this.userAccount=v;}
    public List<Player> getPlayers(){return players;} public void setPlayers(List<Player> v){this.players=v;}
}
