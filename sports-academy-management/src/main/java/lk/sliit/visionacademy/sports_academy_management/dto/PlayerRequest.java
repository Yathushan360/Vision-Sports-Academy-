package lk.sliit.visionacademy.sports_academy_management.dto;
import lk.sliit.visionacademy.sports_academy_management.entity.AccountStatus;
import java.time.LocalDate;
public record PlayerRequest(String playerCode,String fullName,LocalDate dateOfBirth,String ageGroup,String medicalInformation,String kitSize,AccountStatus status,Long parentId,String username,String password) {}
