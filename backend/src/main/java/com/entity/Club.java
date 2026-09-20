package com.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "clubs")
public class Club {

    @Id
    private String id;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "category is required")
    private String category;

    private String logo;
    private String cover;
    private String description;

    @Min(value = 0, message = "membersCount cannot be negative")
    private Integer membersCount;

    public Club() {
    }

    public Club(String id, String name, String category, String logo,
                String cover, String description, Integer membersCount) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.logo = logo;
        this.cover = cover;
        this.description = description;
        this.membersCount = membersCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(Integer membersCount) {
        this.membersCount = membersCount;
    }
}