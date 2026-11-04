package com.example.session07.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CandidateDTO {
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotEmpty(message = "Phải có ít nhất một kỹ năng")
    private List<@NotBlank(message = "Kỹ năng không được để trống") String> skills;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getSkill() {
        return skills;
    }

    public void setSkill(List<String> skills) {
        this.skills = skills;
    }

    public CandidateDTO(String fullName, String email, List<@NotBlank(message = "Kỹ năng không được để trống") String> skills) {
        this.fullName = fullName;
        this.email = email;
        this.skills = skills;
    }

    public CandidateDTO() {
    }
}
