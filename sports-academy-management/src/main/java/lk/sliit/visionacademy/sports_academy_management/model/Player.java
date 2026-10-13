
package lk.sliit.visionacademy.sports_academy_management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lk.sliit.visionacademy.sports_academy_management.entity.AccountStatus;
import lk.sliit.visionacademy.sports_academy_management.entity.ParentGuardian;
import lk.sliit.visionacademy.sports_academy_management.entity.UserAccount;
import java.time.LocalDate;

@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Member 2: existing player fields
    private String name;
    private int age;
    private String playerGroup;
    private String contactNumber;

    // Member 1: additional profile fields
    @Column(name = "player_code", unique = true)
    private String playerCode;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "age_group")
    private String ageGroup;

    @Column(name = "medical_information", length = 500)
    private String medicalInformation;

    @Column(name = "kit_size")
    private String kitSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatus status = AccountStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private ParentGuardian parent;

    @OneToOne
    @JoinColumn(name = "user_account_id", unique = true)
    @JsonIgnore
    private UserAccount userAccount;

    public Player() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (this.fullName == null || this.fullName.isBlank()) {
            this.fullName = name;
        }
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPlayerGroup() {
        return playerGroup;
    }

    public void setPlayerGroup(String playerGroup) {
        this.playerGroup = playerGroup;
        if (this.ageGroup == null || this.ageGroup.isBlank()) {
            this.ageGroup = playerGroup;
        }
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPlayerCode() {
        return playerCode;
    }

    public void setPlayerCode(String playerCode) {
        this.playerCode = playerCode;
    }

    public String getFullName() {
        return fullName != null ? fullName : name;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.name = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAgeGroup() {
        return ageGroup != null ? ageGroup : playerGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
        this.playerGroup = ageGroup;
    }

    public String getMedicalInformation() {
        return medicalInformation;
    }

    public void setMedicalInformation(String medicalInformation) {
        this.medicalInformation = medicalInformation;
    }

    public String getKitSize() {
        return kitSize;
    }

    public void setKitSize(String kitSize) {
        this.kitSize = kitSize;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public ParentGuardian getParent() {
        return parent;
    }

    public void setParent(ParentGuardian parent) {
        this.parent = parent;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
}
