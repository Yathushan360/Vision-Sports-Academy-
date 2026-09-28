package lk.sliit.visionacademy.sports_academy_management.entity;

import jakarta.persistence.*;

@Entity
@Table(name="coaches")
public class Coach {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="coach_code", nullable=false, unique=true) private String coachCode;
    @Column(name="full_name", nullable=false) private String fullName;
    private String phone;
    private String email;
    @Column(name="coaching_license") private String coachingLicense;
    @Column(name="assigned_group") private String assignedGroup;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private AccountStatus status=AccountStatus.ACTIVE;
    @OneToOne @JoinColumn(name="user_account_id", unique=true) private UserAccount userAccount;

    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getCoachCode(){return coachCode;} public void setCoachCode(String v){this.coachCode=v;}
    public String getFullName(){return fullName;} public void setFullName(String v){this.fullName=v;}
    public String getPhone(){return phone;} public void setPhone(String v){this.phone=v;}
    public String getEmail(){return email;} public void setEmail(String v){this.email=v;}
    public String getCoachingLicense(){return coachingLicense;} public void setCoachingLicense(String v){this.coachingLicense=v;}
    public String getAssignedGroup(){return assignedGroup;} public void setAssignedGroup(String v){this.assignedGroup=v;}
    public AccountStatus getStatus(){return status;} public void setStatus(AccountStatus v){this.status=v;}
    public UserAccount getUserAccount(){return userAccount;} public void setUserAccount(UserAccount v){this.userAccount=v;}
}
