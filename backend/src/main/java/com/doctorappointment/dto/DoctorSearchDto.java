package com.doctorappointment.dto;

import java.util.List;

public class DoctorSearchDto {
    private Long id;
    private String fullName;
    private String specialization;
    private String qualification;
    private Double consultationFee;
    private List<ChamberDto> chambers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public Double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(Double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public List<ChamberDto> getChambers() {
        return chambers;
    }

    public void setChambers(List<ChamberDto> chambers) {
        this.chambers = chambers;
    }
}
