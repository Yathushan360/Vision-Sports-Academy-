package lk.sliit.visionacademy.sports_academy_management.dto;
import lk.sliit.visionacademy.sports_academy_management.entity.AccountStatus;
public record ParentRequest(String parentCode,String fullName,String phone,String email,String address,AccountStatus status,String username,String password) {}
