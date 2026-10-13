package lk.sliit.visionacademy.sports_academy_management.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="players")
public class Player {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="player_code", nullable=false, unique=true) private String playerCode;
    @Column(name="full_name", nullable=false) private String fullName;
    @Column(name="date_of_birth") private LocalDate dateOfBirth;
    @Column(name="age_group", nullable=false) private String ageGroup;
    @Column(name="medical_information", length=500) private String medicalInformation;
    @Column(name="kit_size") private String kitSize;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private AccountStatus status=AccountStatus.ACTIVE;
    @ManyToOne @JoinColumn(name="parent_id") private ParentGuardian parent;
    @OneToOne @JoinColumn(name="user_account_id", unique=true) private UserAccount userAccount;

    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getPlayerCode(){return playerCode;} public void setPlayerCode(String v){this.playerCode=v;}
    public String getFullName(){return fullName;} public void setFullName(String v){this.fullName=v;}
    public LocalDate getDateOfBirth(){return dateOfBirth;} public void setDateOfBirth(LocalDate v){this.dateOfBirth=v;}
    public String getAgeGroup(){return ageGroup;} public void setAgeGroup(String v){this.ageGroup=v;}
    public String getMedicalInformation(){return medicalInformation;} public void setMedicalInformation(String v){this.medicalInformation=v;}
    public String getKitSize(){return kitSize;} public void setKitSize(String v){this.kitSize=v;}
    public AccountStatus getStatus(){return status;} public void setStatus(AccountStatus v){this.status=v;}
    public ParentGuardian getParent(){return parent;} public void setParent(ParentGuardian v){this.parent=v;}
    public UserAccount getUserAccount(){return userAccount;} public void setUserAccount(UserAccount v){this.userAccount=v;}
}
