package com.capgemini.training.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "vaccinations")
public class Vaccination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int vaccinationId;

    private String name;
    private String description;
    private BigDecimal price;
    private boolean available;

    @ManyToMany(mappedBy = "vaccinations")
    private List<Pet> pets;

    public Vaccination() {}

    public int getVaccinationId() { return vaccinationId; }
    public void setVaccinationId(int vaccinationId) { this.vaccinationId = vaccinationId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public List<Pet> getPets() { return pets; }
    public void setPets(List<Pet> pets) { this.pets = pets; }
}