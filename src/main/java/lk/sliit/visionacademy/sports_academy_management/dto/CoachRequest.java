package lk.sliit.visionacademy.sports_academy_management.dto;
import lk.sliit.visionacademy.sports_academy_management.entity.AccountStatus;
public record CoachRequest(String coachCode,String fullName,String phone,String email,String coachingLicense,String assignedGroup,AccountStatus status,String username,String password) {}
