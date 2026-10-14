package com.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "elections")
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String position;
    @jakarta.persistence.Column(columnDefinition="TEXT") private String description;
    private String startDate;
    private String endDate;
    private String status;
    @jakarta.persistence.Column(name="eligible_departments",length=300) private String eligibleDepartments="ALL";
    @jakarta.persistence.Column(name="eligible_years",length=100) private String eligibleYears="ALL";
    @jakarta.persistence.Column(name="published_results",nullable=false) private boolean resultsPublished=false;
    @jakarta.persistence.Column(name="created_by",length=254) private String createdBy;
    private java.time.Instant updatedAt;

    public Election() {
    }

    public Election(String title, String position, String startDate, String endDate, String status) {
        this.title = title;
        this.position = position;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getDescription(){return description;}public void setDescription(String v){description=v;}
    public String getEligibleDepartments(){return eligibleDepartments;} public void setEligibleDepartments(String s){eligibleDepartments=s;}
    public String getEligibleYears(){return eligibleYears;} public void setEligibleYears(String s){eligibleYears=s;}
    public boolean isResultsPublished(){return resultsPublished;} public void setResultsPublished(boolean b){resultsPublished=b;}
    public String getCreatedBy(){return createdBy;} public void setCreatedBy(String s){createdBy=s;}
    public java.time.Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(java.time.Instant i){updatedAt=i;}
}
